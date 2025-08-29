package am.armeniabank.walletservicesrc.service;

import am.armeniabank.walletserviceapi.request.WalletOperationRequest;
import am.armeniabank.walletserviceapi.response.WalletResponse;
import am.armeniabank.walletservicesrc.entity.WalletOperation;

import java.util.List;
import java.util.UUID;

public interface WalletOperationService {


    // Операции с балансом
    WalletResponse credit(UUID walletId, WalletOperationRequest reason);
    WalletResponse debit(UUID walletId, WalletOperationRequest reason);
    WalletResponse freeze(UUID walletId, WalletOperationRequest reason);
    WalletResponse unfreeze(UUID walletId, WalletOperationRequest reason);

    // История операций
    List<WalletOperation> getOperations(UUID walletId);
}
