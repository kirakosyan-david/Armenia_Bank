package am.armeniabank.walletservicesrc.service;

import am.armeniabank.armeniabankcommon.request.WalletOperationRequest;
import am.armeniabank.walletserviceapi.response.WalletOperationResponse;

import java.util.List;
import java.util.UUID;

public interface WalletOperationService {


    WalletOperationResponse credit(UUID walletId, WalletOperationRequest reason);
    WalletOperationResponse debit(UUID walletId, WalletOperationRequest reason);
    WalletOperationResponse freeze(UUID walletId, WalletOperationRequest reason);
    WalletOperationResponse unfreeze(UUID walletId, WalletOperationRequest reason);

    List<WalletOperationResponse> getOperations(UUID walletId);
}
