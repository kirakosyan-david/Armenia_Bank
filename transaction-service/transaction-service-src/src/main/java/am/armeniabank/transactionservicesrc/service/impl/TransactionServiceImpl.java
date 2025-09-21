package am.armeniabank.transactionservicesrc.service.impl;

import am.armeniabank.transactionserviceapi.contract.UserApi;
import am.armeniabank.transactionserviceapi.contract.WalletApi;
import am.armeniabank.transactionserviceapi.enums.TransactionState;
import am.armeniabank.transactionserviceapi.enums.TransactionType;
import am.armeniabank.transactionserviceapi.enums.WalletOperationType;
import am.armeniabank.transactionserviceapi.request.TransactionRequest;
import am.armeniabank.transactionserviceapi.request.WalletOperationRequest;
import am.armeniabank.transactionserviceapi.response.TransactionResponse;
import am.armeniabank.transactionserviceapi.response.UserResponse;
import am.armeniabank.transactionserviceapi.response.WalletResponse;
import am.armeniabank.transactionservicesrc.entity.Transaction;
import am.armeniabank.transactionservicesrc.entity.TransactionLog;
import am.armeniabank.transactionservicesrc.exception.custam.InsufficientFundsException;
import am.armeniabank.transactionservicesrc.exception.custam.TransactionFailedException;
import am.armeniabank.transactionservicesrc.exception.custam.TransactionNotFoundException;
import am.armeniabank.transactionservicesrc.exception.custam.UserNotFoundException;
import am.armeniabank.transactionservicesrc.integration.AuditServiceClient;
import am.armeniabank.transactionservicesrc.mapper.TransactionMapper;
import am.armeniabank.transactionservicesrc.repository.TransactionRepository;
import am.armeniabank.transactionservicesrc.service.WalletTransactionService;
import am.armeniabank.transactionservicesrc.util.SecurityUtils;
import am.armeniabank.transactionservicesrc.service.TransactionService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.LocalDateTime;
import java.util.List;
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
    private final WalletTransactionService walletTransactionService;


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

        auditClient.sendAuditTransactionEvent(transaction.getId(), transaction.getFromWalletId(), transaction.getToWalletId(), user, "CREATED");

        return transactionMapper.mapToTransactionResponse(transaction);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TransactionResponse completeTransaction(UUID transactionId) {
        UUID userId = SecurityUtils.getCurrentUserId();
        String token = SecurityUtils.getCurrentToken();

        Transaction transaction = getTransactionId(transactionId);
        try {
            walletTransactionService.debit(transaction, token);
            walletTransactionService.credit(transaction, token);

            transaction.setStatus(TransactionState.COMPLETED);
            transaction.setUpdatedAt(LocalDateTime.now());
            transactionRepository.save(transaction);

            logTransactionCreated(transaction, "COMPLETED");

            UserResponse user = userApi.getUserById(userId, "Bearer " + token);
            if (user != null) {
                auditClient.sendAuditTransactionEvent(transaction.getId(),
                        transaction.getFromWalletId(),
                        transaction.getToWalletId(),
                        user,
                        "COMPLETED");
            }

            return transactionMapper.mapToTransactionResponse(transaction);

        } catch (FeignException.NotFound e) {
            throw new UserNotFoundException("User not found for transaction " + transactionId, e);
        } catch (Exception e) {
            return handleFailedTransaction(transactionId, e, transaction, userId, token);
        }
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TransactionResponse cancelTransaction(UUID transactionId, String token) {
        UUID userId = SecurityUtils.getCurrentUserId();

        Transaction transaction = getTransactionId(transactionId);

        try {
            walletTransactionService.unfreeze(transaction, token);

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
            auditClient.sendAuditTransactionEvent(transaction.getId(), transaction.getFromWalletId(), transaction.getToWalletId(), user, "FAILED");
        }
        return transactionMapper.mapToTransactionResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(UUID transactionId) {
        Transaction transaction = getTransactionId(transactionId);
        return transactionMapper.mapToTransactionResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
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
                auditClient.sendAuditTransactionEvent(
                        transaction.getId(),
                        transaction.getFromWalletId(),
                        transaction.getToWalletId(),
                        user,
                        "FAILED"
                );
            }
        } catch (Exception ex) {
            log.warn("Failed to send audit event for FAILED transaction {}", transactionId, ex);
        }

        throw new TransactionFailedException("Transaction failed", e);
    }
}
