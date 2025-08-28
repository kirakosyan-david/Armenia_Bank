package am.armeniabank.walletservicesrc.service.impl;

import am.armeniabank.walletserviceapi.contract.UserApi;
import am.armeniabank.walletserviceapi.enums.Currency;
import am.armeniabank.walletserviceapi.enums.WalletStatus;
import am.armeniabank.walletserviceapi.response.UserResponse;
import am.armeniabank.walletserviceapi.response.WalletResponse;
import am.armeniabank.walletservicesrc.entity.Wallet;
import am.armeniabank.walletservicesrc.entity.WalletOperation;
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

    @Override
    public WalletResponse createWallet(UUID userId, Currency currency) {

        if (walletRepository.findWalletByUserId(userId).isPresent()) {
            throw new RuntimeException("Wallet already exists for user: " + userId);
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

        return walletMapper.toWalletResponse(walletSaved, userById);
    }

    @Override
    public WalletResponse getWalletById(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(RuntimeException::new);

        String token = SecurityUtils.getCurrentToken();

        UserResponse userById = userApi.getUserById(wallet.getUserId(), "Bearer " + token);

        return walletMapper.toWalletResponse(wallet, userById);
    }

    @Override
    public List<WalletResponse> getWalletsByUserId(UUID userId) {
        return List.of();
    }

    @Override
    public WalletResponse blockWallet(UUID walletId) {
        return null;
    }

    @Override
    public WalletResponse unblockWallet(UUID walletId) {
        return null;
    }

    @Override
    public WalletResponse closeWallet(UUID walletId) {
        return null;
    }

    @Override
    public WalletResponse credit(UUID walletId, BigDecimal amount, String reason) {
        return null;
    }

    @Override
    public WalletResponse debit(UUID walletId, BigDecimal amount, String reason) {
        return null;
    }

    @Override
    public WalletResponse freeze(UUID walletId, BigDecimal amount, String reason) {
        return null;
    }

    @Override
    public WalletResponse unfreeze(UUID walletId, BigDecimal amount, String reason) {
        return null;
    }

    @Override
    public List<WalletOperation> getOperations(UUID walletId) {
        return List.of();
    }


}
