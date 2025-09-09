package am.armeniabank.walletservicesrc.service.impl;

import am.armeniabank.walletserviceapi.contract.UserApi;
import am.armeniabank.walletserviceapi.enums.WalletOperationReason;
import am.armeniabank.walletserviceapi.enums.WalletOperationType;
import am.armeniabank.walletserviceapi.enums.WalletStatus;
import am.armeniabank.walletserviceapi.request.WalletOperationRequest;
import am.armeniabank.walletserviceapi.response.UserResponse;
import am.armeniabank.walletserviceapi.response.WalletOperationResponse;
import am.armeniabank.walletservicesrc.entity.Wallet;
import am.armeniabank.walletservicesrc.entity.WalletOperation;
import am.armeniabank.walletservicesrc.exception.custom.InsufficientBalanceException;
import am.armeniabank.walletservicesrc.exception.custom.InvalidAmountException;
import am.armeniabank.walletservicesrc.exception.custom.WalletBlockedException;
import am.armeniabank.walletservicesrc.exception.custom.WalletNotFoundException;
import am.armeniabank.walletservicesrc.integration.AuditServiceClient;
import am.armeniabank.walletservicesrc.mapper.WalletOperationMapper;
import am.armeniabank.walletservicesrc.repository.WalletOperationRepository;
import am.armeniabank.walletservicesrc.repository.WalletRepository;
import am.armeniabank.walletservicesrc.security.SecurityUtils;
import am.armeniabank.walletservicesrc.service.WalletOperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletOperationServiceImpl implements WalletOperationService {

    private final WalletOperationRepository walletOperationRepository;
    private final WalletRepository walletRepository;
    private final WalletOperationMapper walletOperationMapper;
    private final AuditServiceClient auditClient;
    private final UserApi userApi;

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public WalletOperationResponse credit(UUID walletId, WalletOperationRequest request) {
        log.info("Starting CREDIT operation for walletId={} with amount={}", walletId, request.getAmount());

        Wallet wallet = getWallet(walletId, request);
        validateAmount(request.getAmount());
        updateBalance(wallet, request.getAmount(), WalletOperationType.CREDIT);

        WalletOperation saved = createOperation(wallet, request.getAmount(),
                WalletOperationReason.BONUS, WalletOperationType.CREDIT);

        sendAudit(wallet, "WALLET-CREDITED");

        log.info("CREDIT operation completed successfully for walletId={} newBalance={}", walletId, wallet.getBalance());
        return walletOperationMapper.toWalletOperation(saved);
    }


    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public WalletOperationResponse debit(UUID walletId, WalletOperationRequest request) {
        log.info("Starting DEBIT operation for walletId={} with amount={}", walletId, request.getAmount());

        Wallet wallet = getWallet(walletId, request);

        updateBalance(wallet, request.getAmount(), WalletOperationType.DEBIT);

        WalletOperation saved = createOperation(wallet, request.getAmount(),
                WalletOperationReason.PAYMENT, WalletOperationType.DEBIT);

        sendAudit(wallet, "WALLET-DEBITED");

        log.info("DEBIT operation completed successfully for walletId={} newBalance={}", walletId, wallet.getBalance());
        return walletOperationMapper.toWalletOperation(saved);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public WalletOperationResponse freeze(UUID walletId, WalletOperationRequest request) {
        log.info("Starting FREEZE operation for walletId={} with amount={}", walletId, request.getAmount());

        Wallet wallet = getWallet(walletId, request);

        updateBalance(wallet, request.getAmount(), WalletOperationType.FREEZE);

        WalletOperation saved = createOperation(wallet, request.getAmount(),
                WalletOperationReason.TRANSFER, WalletOperationType.FREEZE);

        sendAudit(wallet, "WALLET-FROZEN");

        log.info("FREEZE operation completed successfully for walletId={} balance={} frozenBalance={}",
                walletId, wallet.getBalance(), wallet.getFrozenBalance());
        return walletOperationMapper.toWalletOperation(saved);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public WalletOperationResponse unfreeze(UUID walletId, WalletOperationRequest request) {
        log.info("Starting UNFREEZE operation for walletId={} with amount={}", walletId, request.getAmount());

        Wallet wallet = getWallet(walletId, request);

        updateBalance(wallet, request.getAmount(), WalletOperationType.UNFREEZE);

        WalletOperation saved = createOperation(wallet, request.getAmount(),
                WalletOperationReason.REFUND, WalletOperationType.UNFREEZE);

        sendAudit(wallet, "WALLET-UNFROZEN");

        log.info("UNFREEZE operation completed successfully for walletId={} balance={} frozenBalance={}",
                walletId, wallet.getBalance(), wallet.getFrozenBalance());
        return walletOperationMapper.toWalletOperation(saved);
    }


    @Override
    @Transactional(readOnly = true)
    public List<WalletOperationResponse> getOperations(UUID walletId) {
        log.debug("Fetching operations for walletId={}", walletId);

        List<Wallet> wallets = walletRepository.findAllById(Collections.singleton(walletId));
        if (wallets.isEmpty()) {
            log.warn("No wallet found with id={} when fetching operations", walletId);
            throw new WalletNotFoundException(walletId);
        }
        UUID targetWalletId = wallets.get(0).getId();

        List<WalletOperationResponse> operations = walletOperationRepository.findByWalletId(targetWalletId)
                .stream()
                .map(walletOperationMapper::toWalletOperation)
                .toList();

        log.info("Found {} operations for walletId={}", operations.size(), walletId);
        return operations;
    }

    private WalletOperation createOperation(Wallet wallet, BigDecimal amount,
                                            WalletOperationReason reason,
                                            WalletOperationType type) {
        log.debug("Creating operation: walletId={}, amount={}, reason={}, type={}",
                wallet.getId(), amount, reason, type);

        WalletOperation operation = new WalletOperation();
        operation.setWallet(wallet);
        operation.setAmount(amount);
        operation.setWalletOperationReason(reason);
        operation.setWalletOperationType(type);
        operation.setCreatedAt(LocalDateTime.now());

        return walletOperationRepository.save(operation);
    }

    private Wallet getWallet(UUID walletId, WalletOperationRequest request) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(walletId));

        if (wallet.getStatus() == WalletStatus.BLOCKED) {
            throw new WalletBlockedException(walletId);
        }

        return wallet;
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(amount);
        }
    }

    private void updateBalance(Wallet wallet, BigDecimal amount, WalletOperationType type) {
        if (amount == null) {
            log.error("Amount is null for walletId={}", wallet.getId());
            throw new IllegalArgumentException("Amount must not be null");
        }

        BigDecimal balance = wallet.getBalance() == null ? BigDecimal.ZERO : wallet.getBalance();
        BigDecimal frozenBalance = wallet.getFrozenBalance() == null ? BigDecimal.ZERO : wallet.getFrozenBalance();

        log.debug("Updating balance for walletId={} type={} amount={} currentBalance={} frozenBalance={}",
                wallet.getId(), type, amount, balance, frozenBalance);

        switch (type) {
            case CREDIT -> wallet.setBalance(balance.add(amount));
            case DEBIT -> {
                if (balance.compareTo(amount) < 0) {
                    log.warn("Insufficient balance for DEBIT: walletId={} balance={} amount={}",
                            wallet.getId(), balance, amount);
                    throw new InsufficientBalanceException(wallet.getId(), balance, amount);
                }
                wallet.setBalance(balance.subtract(amount));
            }
            case FREEZE -> {
                if (balance.compareTo(amount) < 0) {
                    log.warn("Insufficient balance to FREEZE: walletId={} balance={} amount={}",
                            wallet.getId(), balance, amount);
                    throw new InsufficientBalanceException(wallet.getId(), balance, amount);
                }
                wallet.setBalance(balance.subtract(amount));
                wallet.setFrozenBalance(frozenBalance.add(amount));
            }
            case UNFREEZE -> {
                if (frozenBalance.compareTo(amount) < 0) {
                    log.warn("Insufficient frozen funds to UNFREEZE: walletId={} frozenBalance={} amount={}",
                            wallet.getId(), frozenBalance, amount);
                    throw new InsufficientBalanceException(wallet.getId(), balance, amount);
                }
                wallet.setFrozenBalance(frozenBalance.subtract(amount));
                wallet.setBalance(balance.add(amount));
            }
            default -> throw new UnsupportedOperationException("Unsupported operation type: " + type);
        }

        walletRepository.save(wallet);
        log.debug("Balance updated for walletId={} newBalance={} newFrozenBalance={}",
                wallet.getId(), wallet.getBalance(), wallet.getFrozenBalance());
    }

    private void sendAudit(Wallet wallet, String action) {
        String token = SecurityUtils.getCurrentToken();
        UserResponse userById = userApi.getUserById(wallet.getUserId(), "Bearer " + token);
        auditClient.sendAuditWalletEvent(wallet.getId(), userById, action);
    }
}
