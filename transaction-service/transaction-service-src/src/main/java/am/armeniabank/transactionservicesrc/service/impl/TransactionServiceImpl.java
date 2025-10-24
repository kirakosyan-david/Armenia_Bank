package am.armeniabank.transactionservicesrc.service.impl;

import am.armeniabank.transactionserviceapi.contract.UserApi;
import am.armeniabank.transactionserviceapi.contract.WalletApi;
import am.armeniabank.transactionserviceapi.enums.FreezeStatus;
import am.armeniabank.transactionserviceapi.enums.TransactionState;
import am.armeniabank.transactionserviceapi.enums.TransactionType;
import am.armeniabank.transactionserviceapi.request.TransactionRequest;
import am.armeniabank.transactionserviceapi.response.FreezeResponse;
import am.armeniabank.transactionserviceapi.response.TransactionResponse;
import am.armeniabank.transactionserviceapi.response.UserResponse;
import am.armeniabank.transactionserviceapi.response.WalletResponse;
import am.armeniabank.transactionservicesrc.entity.Freeze;
import am.armeniabank.transactionservicesrc.entity.Transaction;
import am.armeniabank.transactionservicesrc.entity.TransactionLog;
import am.armeniabank.transactionservicesrc.exception.custam.InsufficientFundsException;
import am.armeniabank.transactionservicesrc.exception.custam.TransactionFailedException;
import am.armeniabank.transactionservicesrc.exception.custam.TransactionNotFoundException;
import am.armeniabank.transactionservicesrc.exception.custam.UserNotFoundException;
import am.armeniabank.transactionservicesrc.integration.AuditServiceClient;
import am.armeniabank.transactionservicesrc.integration.NotificationServiceClient;
import am.armeniabank.transactionservicesrc.kafka.model.TransactionEvent;
import am.armeniabank.transactionservicesrc.mapper.TransactionMapper;
import am.armeniabank.transactionservicesrc.repository.TransactionRepository;
import am.armeniabank.transactionservicesrc.service.FreezeService;
import am.armeniabank.transactionservicesrc.service.TransactionService;
import am.armeniabank.transactionservicesrc.service.WalletTransactionService;
import am.armeniabank.transactionservicesrc.util.SecurityUtils;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final UserApi userApi;
    private final WalletApi walletApi;
    private final AuditServiceClient auditClient;
    private final NotificationServiceClient notificationServiceClient;
    private final WalletTransactionService walletTransactionService;
    private final FreezeService freezeService;
    private final CacheManager cacheManager;
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TransactionResponse createTransaction(TransactionRequest request) {
        String token = SecurityUtils.getCurrentToken();

        UUID currentUserId = SecurityUtils.getCurrentUserId();

        UserResponse user = userApi.getUserById(currentUserId, "Bearer " + token);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        WalletResponse walletInfo = walletApi.getWalletInfo(request.getFromWalletId(), "Bearer " + token);
        if (walletInfo.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        walletTransactionService.freeze(request, token);

        Transaction transaction = Transaction.builder()
                .fromWalletId(request.getFromWalletId())
                .toWalletId(request.getToWalletId())
                .userId(currentUserId)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .type(TransactionType.TRANSFER)
                .status(TransactionState.CREATED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);

        FreezeResponse freeze = freezeService.createFreeze(transaction, request.getAmount(),
                "Transaction created", token);

        sendTransactionEvent(transaction, "Initial transaction freeze");

        sendTransactionNotifications(transaction, token);

        log.info("Freeze created: id={}, walletId={}, amount={}", freeze.getId(), freeze.getWalletId(), freeze.getAmount());

        auditClient.sendAuditTransactionEvent(transaction.getId(), transaction.getFromWalletId(),
                transaction.getToWalletId(), user, "CREATED");

        Cache transactionsCache = cacheManager.getCache("transactions");
        if (transactionsCache != null) {
            transactionsCache.put(transaction.getId(), transactionMapper.mapToTransactionResponse(transaction));
        }

        return transactionMapper.mapToTransactionResponse(transaction);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TransactionResponse completeTransaction(UUID transactionId) {
        UUID userId = SecurityUtils.getCurrentUserId();
        String token = SecurityUtils.getCurrentToken();

        Transaction transaction = getTransactionId(transactionId);
        try {
            walletTransactionService.debit(transaction, token);
            walletTransactionService.credit(transaction, token);

            Freeze freeze = freezeService.getFreezeByTransaction(transactionId);
            freezeService.completeFreeze(freeze);
            transaction.setStatus(TransactionState.COMPLETED);
            transaction.setUpdatedAt(LocalDateTime.now());
            transactionRepository.save(transaction);

            Optional.ofNullable(cacheManager.getCache("transactions"))
                    .ifPresent(cache -> cache.put(transactionId, transactionMapper.mapToTransactionResponse(transaction)));

            Optional.ofNullable(cacheManager.getCache("wallets"))
                    .ifPresent(cache -> {
                        cache.evict(transaction.getFromWalletId());
                        cache.evict(transaction.getToWalletId());
                    });

            logTransactionCreated(transaction, "COMPLETED");

            UserResponse user = userApi.getUserById(userId, "Bearer " + token);
            if (user != null) {
                auditClient.sendAuditTransactionEvent(transaction.getId(),
                        transaction.getFromWalletId(),
                        transaction.getToWalletId(),
                        user,
                        "COMPLETED");
            }

            sendTransactionNotifications(transaction, token);

            sendTransactionEvent(transaction, "Transaction completed");

            updateCaches(transaction);

            return transactionMapper.mapToTransactionResponse(transaction);

        } catch (FeignException.NotFound e) {
            throw new UserNotFoundException("User not found for transaction " + transactionId, e);
        } catch (Exception e) {
            return handleFailedTransaction(transactionId, e, transaction, userId, token);
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TransactionResponse cancelTransaction(UUID transactionId, String token) {
        UUID userId = SecurityUtils.getCurrentUserId();

        Transaction transaction = getTransactionId(transactionId);

        try {
            walletTransactionService.unfreeze(transaction, token);

            Freeze freeze = freezeService.getFreezeByTransaction(transactionId);
            FreezeResponse freezeResponse = freezeService.releaseFreeze(freeze, token);
            log.info("Released freeze {} for transaction {} and wallet {}",
                    freezeResponse.getId(),
                    transaction.getId(),
                    freezeResponse.getWalletId());

            transaction.setStatus(TransactionState.ROLLED_BACK);
            transaction.setUpdatedAt(LocalDateTime.now());
            transactionRepository.save(transaction);

            logTransactionCreated(transaction, "ROLLED_BACK");

            UserResponse user = userApi.getUserById(userId, "Bearer " + token);
            if (user != null) {
                auditClient.sendAuditTransactionEvent(transaction.getId(),
                        transaction.getFromWalletId(),
                        transaction.getToWalletId(),
                        user,
                        "ROLLED_BACK");
            }

            sendTransactionNotifications(transaction, token);

            sendTransactionEvent(transaction, "Transaction rolled back");

            updateCaches(transaction);

            return transactionMapper.mapToTransactionResponse(transaction);
        } catch (Exception e) {
            return handleFailedTransaction(transactionId, e, transaction, userId, token);
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TransactionResponse failTransaction(UUID transactionId, String token) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Transaction transaction = getTransactionId(transactionId);

        transaction.setStatus(TransactionState.FAILED);
        transaction.setUpdatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);

        logTransactionCreated(transaction, "FAILED");

        UserResponse user = userApi.getUserById(userId, "Bearer " + token);
        if (user != null) {
            auditClient.sendAuditTransactionEvent(transaction.getId(),
                    transaction.getFromWalletId(),
                    transaction.getToWalletId(),
                    user,
                    "FAILED");
        }

        try {
            Freeze freeze = freezeService.getFreezeByTransaction(transactionId);
            if (freeze != null && freeze.getFreezeStatus() == FreezeStatus.ACTIVE) {
                FreezeResponse freezeResponse = freezeService.releaseFreeze(freeze, token);
                log.info("Freeze released: id={}, status={}, transactionId={}, walletId={}",
                        freezeResponse.getId(),
                        freezeResponse.getFreezeStatus(),
                        transaction.getId(),
                        freezeResponse.getWalletId());
            }
        } catch (Exception ex) {
            log.warn("Freeze release failed for FAILED transaction {}", transactionId, ex);
        }

        sendTransactionNotifications(transaction, token);

        sendTransactionEvent(transaction, "Transaction failed");

        updateCaches(transaction);

        return transactionMapper.mapToTransactionResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "transactions", key = "#transactionId")
    public TransactionResponse getTransactionById(UUID transactionId) {
        Transaction transaction = getTransactionId(transactionId);
        return transactionMapper.mapToTransactionResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "wallets", key = "#walletId")
    public List<TransactionResponse> getTransactionsByWallet(UUID walletId) {
        return transactionRepository.findByFromWalletIdOrToWalletId(walletId, walletId)
                .stream()
                .map(transactionMapper::mapToTransactionResponse)
                .toList();
    }

    private void logTransactionCreated(Transaction transaction, String message) {
        TransactionLog log = TransactionLog.builder()
                .transaction(transaction)
                .eventType(transaction.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();

        transaction.getLogs().add(log);
        transactionRepository.save(transaction);
    }

    private Transaction getTransactionId(UUID transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
    }

    private TransactionResponse handleFailedTransaction(UUID transactionId, Exception e,
                                                        Transaction transaction,
                                                        UUID userId,
                                                        String token) {
        transaction.setStatus(TransactionState.FAILED);
        transaction.setUpdatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);

        log.error("Error completing transaction {}", transactionId, e);

        try {
            UserResponse user = userApi.getUserById(userId, "Bearer " + token);
            if (user != null) {
                auditClient.sendAuditTransactionEvent(transaction.getId(),
                        transaction.getFromWalletId(),
                        transaction.getToWalletId(),
                        user,
                        "FAILED");
            }
        } catch (Exception ex) {
            log.warn("Failed to send audit event for FAILED transaction {}", transactionId, ex);
        }

        try {
            Freeze freeze = freezeService.getFreezeByTransaction(transactionId);
            if (freeze != null && freeze.getFreezeStatus() == am.armeniabank.transactionserviceapi.enums.FreezeStatus.ACTIVE) {
                FreezeResponse freezeResponse = freezeService.releaseFreeze(freeze, token);

                log.info("Freeze released: id={}, status={}, transactionId={}, walletId={}",
                        freezeResponse.getId(),
                        freezeResponse.getFreezeStatus(),
                        transaction.getId(),
                        freezeResponse.getWalletId());
            }
        } catch (Exception ex) {
            log.warn("Freeze release failed for FAILED transaction {}", transactionId, ex);
        }

        throw new TransactionFailedException("Transaction failed", e);
    }

    private void updateCaches(Transaction transaction) {
        Cache transactionsCache = cacheManager.getCache("transactions");
        if (transactionsCache != null) {
            transactionsCache.put(transaction.getId(), transactionMapper.mapToTransactionResponse(transaction));
        }

        Cache walletsCache = cacheManager.getCache("wallets");
        if (walletsCache != null) {
            walletsCache.evict(transaction.getFromWalletId());
            walletsCache.evict(transaction.getToWalletId());
        }
    }

    private void sendTransactionEvent(Transaction transaction, String reason) {
        TransactionEvent event = TransactionEvent.builder()
                .transactionId(transaction.getId())
                .fromWalletId(transaction.getFromWalletId())
                .toWalletId(transaction.getToWalletId())
                .userId(transaction.getUserId())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .timestamp(LocalDateTime.now())
                .reason(reason)
                .build();
        kafkaTemplate.send("transaction-events", event);
    }

    private void sendTransactionNotifications(Transaction transaction, String token) {
        try {
            BigDecimal senderBalance = walletApi.getWalletInfo(transaction.getFromWalletId(), "Bearer " + token).getBalance();
            BigDecimal receiverBalance = walletApi.getWalletInfo(transaction.getToWalletId(), "Bearer " + token).getBalance();

            String senderMessage = "Your transaction " + transaction.getId() + " of " +
                    transaction.getAmount() + " " + transaction.getCurrency() +
                    " is " + transaction.getStatus() +
                    ". Your current balance: " + senderBalance + " " + transaction.getCurrency();

            String receiverMessage = "You received " + transaction.getAmount() + " " + transaction.getCurrency() +
                    " from wallet " + transaction.getFromWalletId() +
                    ". Your current balance: " + receiverBalance + " " + transaction.getCurrency();

            notificationServiceClient.sendTransactionNotification(
                    transaction.getUserId(),         // senderId
                    transaction.getToWalletId(),     // receiverId
                    transaction.getAmount(),
                    transaction.getCurrency(),
                    senderMessage,
                    receiverMessage,
                    token
            );

            log.info("Transaction notifications sent for transaction {}", transaction.getId());
        } catch (Exception ex) {
            log.error("Failed to send transaction notifications for transaction {}: {}", transaction.getId(), ex.getMessage(), ex);
        }
    }

}
