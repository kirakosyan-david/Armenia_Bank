package am.armeniabank.walletservicesrc.service.impl;

import am.armeniabank.walletserviceapi.contract.UserApi;
import am.armeniabank.walletserviceapi.enums.Currency;
import am.armeniabank.walletserviceapi.enums.WalletStatus;
import am.armeniabank.walletserviceapi.response.UserResponse;
import am.armeniabank.walletserviceapi.response.WalletResponse;
import am.armeniabank.walletservicesrc.entity.Wallet;
import am.armeniabank.walletservicesrc.integration.AuditServiceClient;
import am.armeniabank.walletservicesrc.kafka.model.AuditEvent;
import am.armeniabank.walletservicesrc.mapper.WalletMapper;
import am.armeniabank.walletservicesrc.repository.WalletRepository;
import am.armeniabank.walletservicesrc.security.SecurityUtils;
import am.armeniabank.walletservicesrc.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;
    private final UserApi userApi;
    private final AuditServiceClient auditClient;

    @Override
    public WalletResponse createWallet(UUID userId, Currency currency) {

        if (walletRepository.findByUserIdAndCurrency(userId, currency).isPresent()) {
            throw new RuntimeException("Wallet already exists for user: " + userId + " with currency: " + currency);
        }

        String token = SecurityUtils.getCurrentToken();

        UserResponse userById = userApi.getUserById(userId, "Bearer " + token);

        Wallet wallet = Wallet.builder()
                .userId(userId)
                .balance(BigDecimal.ZERO)
                .currency(currency)
                .status(WalletStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Wallet walletSaved = walletRepository.save(wallet);

        auditClient.sendAuditWalletEvent(wallet.getId(), userById, "WALLET-CREATED");

        return walletMapper.toWalletResponse(walletSaved, userById);
    }

    @Override
    public WalletResponse getWalletById(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found: " + walletId));

        String token = SecurityUtils.getCurrentToken();

        UserResponse userById = userApi.getUserById(wallet.getUserId(), "Bearer " + token);

        return walletMapper.toWalletResponse(wallet, userById);
    }

    @Override
    public List<WalletResponse> getWalletsByUserId(UUID userId) {
        List<Wallet> wallets = walletRepository.findAllByUserId(userId);
        String token = SecurityUtils.getCurrentToken();

        UserResponse userById = userApi.getUserById(userId, "Bearer " + token);

        return wallets.stream()
                .map(wallet -> walletMapper.toWalletResponse(wallet, userById))
                .toList();
    }

    @Override
    public WalletResponse blockWallet(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found: " + walletId));
        wallet.setStatus(WalletStatus.BLOCKED);
        walletRepository.save(wallet);
        String token = SecurityUtils.getCurrentToken();

        UserResponse userById = userApi.getUserById(wallet.getUserId(), "Bearer " + token);

        auditClient.sendAuditWalletEvent(wallet.getId(), userById, "WALLET-BLOCKED");
        return walletMapper.toWalletResponse(wallet, userById);
    }

    @Override
    public WalletResponse unblockWallet(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found: " + walletId));
        wallet.setStatus(WalletStatus.ACTIVE);
        walletRepository.save(wallet);
        String token = SecurityUtils.getCurrentToken();

        UserResponse userById = userApi.getUserById(wallet.getUserId(), "Bearer " + token);

        auditClient.sendAuditWalletEvent(wallet.getId(), userById, "WALLET-UNBLOCKED");

        return walletMapper.toWalletResponse(wallet, userById);
    }

    @Override
    public WalletResponse closeWallet(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found: " + walletId));
        wallet.setStatus(WalletStatus.CLOSED);
        walletRepository.save(wallet);
        String token = SecurityUtils.getCurrentToken();

        UserResponse userById = userApi.getUserById(wallet.getUserId(), "Bearer " + token);

        auditClient.sendAuditWalletEvent(wallet.getId(), userById, "WALLET-CLOSED");

        return walletMapper.toWalletResponse(wallet, userById);
    }

}
