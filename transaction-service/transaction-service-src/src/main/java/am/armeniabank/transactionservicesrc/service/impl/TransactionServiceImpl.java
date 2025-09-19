package am.armeniabank.transactionservicesrc.service.impl;

import am.armeniabank.transactionserviceapi.contract.UserApi;
import am.armeniabank.transactionserviceapi.contract.WalletApi;
import am.armeniabank.transactionserviceapi.enums.TransactionState;
import am.armeniabank.transactionserviceapi.request.TransactionRequest;
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
import am.armeniabank.transactionservicesrc.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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


    @Override
    public TransactionResponse createTransaction(TransactionRequest request) {
        UserResponse user = userApi.getUserById(request.getUserId(), request.getAuthToken());
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        WalletResponse walletInfo = walletApi.getWalletInfo(request.getFromWalletId());
        if (walletInfo.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        walletApi.freezeWallet(request.getFromWalletId(), request.getAmount());
        Transaction transaction = Transaction.builder()
                .fromWalletId(request.getFromWalletId())
                .toWalletId(request.getToWalletId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .type(request.getType())
                .status(TransactionState.CREATED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);

        auditClient.sendAuditTransactionEvent(transaction.getId(), user, "CREATED");

        return transactionMapper.mapToTransactionResponse(transaction);
    }

    @Override
    public TransactionResponse completeTransaction(UUID transactionId, String authToken) {
        Transaction transaction = getTransactionId(transactionId);
        try {

            walletApi.debitWallet(transaction.getFromWalletId(), transaction.getAmount());
            walletApi.creditWallet(transaction.getToWalletId(), transaction.getAmount());

            transaction.setStatus(TransactionState.COMPLETED);
            transaction.setUpdatedAt(LocalDateTime.now());
            transactionRepository.save(transaction);

            logTransactionCreated(transaction, "COMPLETED");

            UserResponse user = userApi.getUserById(transaction.getUserId(), authToken);
            if (user != null) {
                auditClient.sendAuditTransactionEvent(transaction.getId(), user, "COMPLETED");
            }

            return transactionMapper.mapToTransactionResponse(transaction);

        } catch (Exception e) {

            return handleFailedTransaction(transactionId, e, transaction, authToken);

        }
    }

    @Override
    public TransactionResponse cancelTransaction(UUID transactionId, String authToken) {
        Transaction transaction = getTransactionId(transactionId);

        try {
            walletApi.debitWallet(transaction.getFromWalletId(), transaction.getAmount());
            walletApi.creditWallet(transaction.getToWalletId(), transaction.getAmount());

            transaction.setStatus(TransactionState.ROLLED_BACK);
            transaction.setUpdatedAt(LocalDateTime.now());
            transactionRepository.save(transaction);

            logTransactionCreated(transaction, "ROLLED_BACK");

            UserResponse user = userApi.getUserById(transaction.getUserId(), authToken);
            if (user != null) {
                auditClient.sendAuditTransactionEvent(transaction.getId(), user, "ROLLED_BACK");
            }

            return transactionMapper.mapToTransactionResponse(transaction);
        } catch (Exception e) {

            return handleFailedTransaction(transactionId, e, transaction, authToken);
        }
    }

    @Override
    public TransactionResponse failTransaction(UUID transactionId, String authToken) {
        Transaction transaction = getTransactionId(transactionId);

        transaction.setStatus(TransactionState.FAILED);
        transaction.setUpdatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);

        logTransactionCreated(transaction, "FAILED");

        UserResponse user = userApi.getUserById(transaction.getFromWalletId(), authToken);
        if (user != null) {
            auditClient.sendAuditTransactionEvent(transaction.getId(), user, "FAILED");
        }
        return transactionMapper.mapToTransactionResponse(transaction);
    }

    @Override
    public TransactionResponse getTransactionById(UUID transactionId) {
        Transaction transaction = getTransactionId(transactionId);
        return transactionMapper.mapToTransactionResponse(transaction);
    }

    @Override
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

    private TransactionResponse handleFailedTransaction(UUID transactionId, Exception e, Transaction transaction, String authToken) {
        transaction.setStatus(TransactionState.FAILED);
        transaction.setUpdatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);

        log.error("Error completing transaction {}", transactionId, e);

        try {
            UserResponse user = userApi.getUserById(transaction.getFromWalletId(), authToken);
            if (user != null) {
                auditClient.sendAuditTransactionEvent(transaction.getId(), user, "FAILED");
            }
        } catch (Exception ex) {
            log.warn("Failed to send audit event for FAILED transaction {}", transactionId, ex);
        }

        throw new TransactionFailedException("Transaction failed", e);
    }
}
