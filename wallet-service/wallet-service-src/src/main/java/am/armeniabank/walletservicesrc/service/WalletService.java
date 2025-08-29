package am.armeniabank.walletservicesrc.service;

import am.armeniabank.walletserviceapi.enums.Currency;
import am.armeniabank.walletserviceapi.response.WalletResponse;

import java.util.List;
import java.util.UUID;

public interface WalletService {

    // Управление кошельками
    WalletResponse createWallet(UUID userId, Currency currency);
    WalletResponse getWalletById(UUID walletId);
    List<WalletResponse> getWalletsByUserId(UUID userId);
    WalletResponse blockWallet(UUID walletId);
    WalletResponse unblockWallet(UUID walletId);
    WalletResponse closeWallet(UUID walletId);

}
