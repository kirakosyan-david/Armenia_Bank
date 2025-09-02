package am.armeniabank.walletservicesrc.service.impl;

import am.armeniabank.walletserviceapi.enums.WalletOperationReason;
import am.armeniabank.walletserviceapi.enums.WalletOperationType;
import am.armeniabank.walletserviceapi.request.WalletOperationRequest;
import am.armeniabank.walletserviceapi.response.WalletOperationResponse;
import am.armeniabank.walletservicesrc.entity.Wallet;
import am.armeniabank.walletservicesrc.entity.WalletOperation;
import am.armeniabank.walletservicesrc.mapper.WalletOperationMapper;
import am.armeniabank.walletservicesrc.repository.WalletOperationRepository;
import am.armeniabank.walletservicesrc.repository.WalletRepository;
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

    @Override
    @Transactional
    public WalletOperationResponse credit(UUID walletId, WalletOperationRequest request) {
        if (request.getAmount() == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found: " + walletId));

        BigDecimal currentBalance = wallet.getBalance() == null ? BigDecimal.ZERO : wallet.getBalance();
        wallet.setBalance(currentBalance.add(request.getAmount()));
        walletRepository.save(wallet);

        WalletOperation operation = new WalletOperation();
        operation.setWallet(wallet);
        operation.setAmount(request.getAmount());
        operation.setWalletOperationReason(WalletOperationReason.BONUS);
        operation.setWalletOperationType(WalletOperationType.CREDIT);
        operation.setCreatedAt(LocalDateTime.now());

        WalletOperation saved = walletOperationRepository.save(operation);

        return walletOperationMapper.toWalletOperation(saved);
    }


    @Override
    @Transactional
    public WalletOperationResponse debit(UUID walletId, WalletOperationRequest reason) {
        if (reason.getAmount() == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found: " + walletId));

        BigDecimal currentBalance = wallet.getBalance() == null ? BigDecimal.ZERO : wallet.getBalance();
        if (currentBalance.compareTo(reason.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(currentBalance.subtract(reason.getAmount()));
        walletRepository.save(wallet);

        WalletOperation operation = new WalletOperation();
        operation.setWallet(wallet);
        operation.setAmount(reason.getAmount());
        operation.setWalletOperationReason(WalletOperationReason.PAYMENT);
        operation.setWalletOperationType(WalletOperationType.DEBIT);
        operation.setCreatedAt(LocalDateTime.now());

        WalletOperation saved = walletOperationRepository.save(operation);
        return walletOperationMapper.toWalletOperation(saved);
    }

    @Override
    @Transactional
    public WalletOperationResponse freeze(UUID walletId, WalletOperationRequest reason) {
        if (reason.getAmount() == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found: " + walletId));

        BigDecimal currentBalance = wallet.getBalance() == null ? BigDecimal.ZERO : wallet.getBalance();
        if (currentBalance.compareTo(reason.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(currentBalance.subtract(reason.getAmount()));
        walletRepository.save(wallet);

        WalletOperation operation = new WalletOperation();
        operation.setWallet(wallet);
        operation.setAmount(reason.getAmount());
        operation.setWalletOperationReason(WalletOperationReason.TRANSFER);
        operation.setWalletOperationType(WalletOperationType.FREEZE);
        operation.setCreatedAt(LocalDateTime.now());

        WalletOperation saved = walletOperationRepository.save(operation);
        return walletOperationMapper.toWalletOperation(saved);
    }

    @Override
    @Transactional
    public WalletOperationResponse unfreeze(UUID walletId, WalletOperationRequest reason) {
        if (reason.getAmount() == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found: " + walletId));

        BigDecimal currentBalance = wallet.getBalance() == null ? BigDecimal.ZERO : wallet.getBalance();
        if (currentBalance.compareTo(reason.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        wallet.setBalance(currentBalance.add(reason.getAmount()));
        walletRepository.save(wallet);

        WalletOperation operation = new WalletOperation();
        operation.setWallet(wallet);
        operation.setAmount(reason.getAmount());
        operation.setWalletOperationReason(WalletOperationReason.REFUND);
        operation.setWalletOperationType(WalletOperationType.UNFREEZE);
        operation.setCreatedAt(LocalDateTime.now());

        WalletOperation saved = walletOperationRepository.save(operation);

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
}
