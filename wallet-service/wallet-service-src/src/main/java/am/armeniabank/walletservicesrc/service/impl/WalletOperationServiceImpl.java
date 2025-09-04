package am.armeniabank.walletservicesrc.service.impl;

import am.armeniabank.walletserviceapi.contract.UserApi;
import am.armeniabank.walletserviceapi.enums.WalletOperationReason;
import am.armeniabank.walletserviceapi.enums.WalletOperationType;
import am.armeniabank.walletserviceapi.request.WalletOperationRequest;
import am.armeniabank.walletserviceapi.response.UserResponse;
import am.armeniabank.walletserviceapi.response.WalletOperationResponse;
import am.armeniabank.walletservicesrc.entity.Wallet;
import am.armeniabank.walletservicesrc.entity.WalletOperation;
import am.armeniabank.walletservicesrc.integration.AuditServiceClient;
import am.armeniabank.walletservicesrc.mapper.WalletOperationMapper;
import am.armeniabank.walletservicesrc.repository.WalletOperationRepository;
import am.armeniabank.walletservicesrc.repository.WalletRepository;
import am.armeniabank.walletservicesrc.security.SecurityUtils;
import am.armeniabank.walletservicesrc.service.WalletOperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletOperationServiceImpl implements WalletOperationService {

    private final WalletOperationRepository walletOperationRepository;
    private final WalletRepository walletRepository;
    private final WalletOperationMapper walletOperationMapper;
    private final AuditServiceClient auditClient;
    private final UserApi userApi;

    @Override
    @Transactional
    public WalletOperationResponse credit(UUID walletId, WalletOperationRequest request) {
        Wallet wallet = getWallet(walletId, request);

        updateBalance(wallet, request.getAmount(), WalletOperationType.CREDIT);

        WalletOperation saved = createOperation(wallet, request.getAmount(),
                WalletOperationReason.BONUS, WalletOperationType.CREDIT);

        String token = SecurityUtils.getCurrentToken();

        UserResponse userById = userApi.getUserById(wallet.getUserId(), "Bearer " + token);

        auditClient.sendAuditWalletEvent(wallet.getId(), userById, "WALLET-CREDITED");

        return walletOperationMapper.toWalletOperation(saved);
    }


    @Override
    @Transactional
    public WalletOperationResponse debit(UUID walletId, WalletOperationRequest request) {
        Wallet wallet = getWallet(walletId, request);

        updateBalance(wallet, request.getAmount(), WalletOperationType.DEBIT);

        WalletOperation saved = createOperation(wallet, request.getAmount(),
                WalletOperationReason.PAYMENT, WalletOperationType.DEBIT);

        String token = SecurityUtils.getCurrentToken();

        UserResponse userById = userApi.getUserById(wallet.getUserId(), "Bearer " + token);

        auditClient.sendAuditWalletEvent(wallet.getId(), userById, "WALLET-DEBITED");

        return walletOperationMapper.toWalletOperation(saved);
    }

    @Override
    @Transactional
    public WalletOperationResponse freeze(UUID walletId, WalletOperationRequest request) {
        Wallet wallet = getWallet(walletId, request);

        updateBalance(wallet, request.getAmount(), WalletOperationType.FREEZE);

        WalletOperation saved = createOperation(wallet, request.getAmount(),
                WalletOperationReason.TRANSFER, WalletOperationType.FREEZE);

        String token = SecurityUtils.getCurrentToken();

        UserResponse userById = userApi.getUserById(wallet.getUserId(), "Bearer " + token);

        auditClient.sendAuditWalletEvent(wallet.getId(), userById, "WALLET-FROZEN");

        return walletOperationMapper.toWalletOperation(saved);
    }

    @Override
    @Transactional
    public WalletOperationResponse unfreeze(UUID walletId, WalletOperationRequest request) {
        Wallet wallet = getWallet(walletId, request);

        updateBalance(wallet, request.getAmount(), WalletOperationType.UNFREEZE);

        WalletOperation saved = createOperation(wallet, request.getAmount(),
                WalletOperationReason.REFUND, WalletOperationType.UNFREEZE);

        String token = SecurityUtils.getCurrentToken();

        UserResponse userById = userApi.getUserById(wallet.getUserId(), "Bearer " + token);

        auditClient.sendAuditWalletEvent(wallet.getId(), userById, "WALLET-UNFROZEN");

        return walletOperationMapper.toWalletOperation(saved);
    }


    @Override
    public List<WalletOperationResponse> getOperations(UUID walletId) {
        List<Wallet> wallets = walletRepository.findAllById(Collections.singleton(walletId));
        if (wallets.isEmpty()) {
            return Collections.emptyList();
        }
        UUID targetWalletId = wallets.get(0).getId();

        return walletOperationRepository.findByWalletId(targetWalletId)
                .stream()
                .map(walletOperationMapper::toWalletOperation)
                .toList();
    }

    private WalletOperation createOperation(Wallet wallet, BigDecimal amount,
                                            WalletOperationReason reason,
                                            WalletOperationType type) {
        WalletOperation operation = new WalletOperation();
        operation.setWallet(wallet);
        operation.setAmount(amount);
        operation.setWalletOperationReason(reason);
        operation.setWalletOperationType(type);
        operation.setCreatedAt(LocalDateTime.now());

        return walletOperationRepository.save(operation);
    }

    private Wallet getWallet(UUID walletId, WalletOperationRequest request) {
        if (request.getAmount() == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found: " + walletId));

        return wallet;
    }

    private void updateBalance(Wallet wallet, BigDecimal amount, WalletOperationType type) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }

        BigDecimal balance = wallet.getBalance() == null ? BigDecimal.ZERO : wallet.getBalance();
        BigDecimal frozenBalance = wallet.getFrozenBalance() == null ? BigDecimal.ZERO : wallet.getFrozenBalance();

        switch (type) {
            case CREDIT -> wallet.setBalance(balance.add(amount));
            case DEBIT -> {
                if (balance.compareTo(amount) < 0) {
                    throw new RuntimeException("Insufficient balance");
                }
                wallet.setBalance(balance.subtract(amount));
            }
            case FREEZE -> {
                if (balance.compareTo(amount) < 0) {
                    throw new RuntimeException("Insufficient balance to freeze");
                }
                wallet.setBalance(balance.subtract(amount));
                wallet.setFrozenBalance(frozenBalance.add(amount));
            }
            case UNFREEZE -> {
                if (frozenBalance.compareTo(amount) < 0) {
                    throw new RuntimeException("Insufficient frozen funds to unfreeze");
                }
                wallet.setFrozenBalance(frozenBalance.subtract(amount));
                wallet.setBalance(balance.add(amount));
            }
            default -> throw new UnsupportedOperationException("Unsupported operation type: " + type);
        }

        walletRepository.save(wallet);
    }
}
