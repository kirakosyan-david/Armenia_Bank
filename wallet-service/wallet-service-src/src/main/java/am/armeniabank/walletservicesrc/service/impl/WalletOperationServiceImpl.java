package am.armeniabank.walletservicesrc.service.impl;

import am.armeniabank.walletserviceapi.request.WalletOperationRequest;
import am.armeniabank.walletserviceapi.response.WalletResponse;
import am.armeniabank.walletservicesrc.entity.WalletOperation;
import am.armeniabank.walletservicesrc.service.WalletOperationService;

import java.util.List;
import java.util.UUID;

public class WalletOperationServiceImpl implements WalletOperationService {


    @Override
    public WalletResponse credit(UUID walletId, WalletOperationRequest reason) {
        return null;
    }

    @Override
    public WalletResponse debit(UUID walletId, WalletOperationRequest reason) {
        return null;
    }

    @Override
    public WalletResponse freeze(UUID walletId, WalletOperationRequest reason) {
        return null;
    }

    @Override
    public WalletResponse unfreeze(UUID walletId, WalletOperationRequest reason) {
        return null;
    }

    @Override
    public List<WalletOperation> getOperations(UUID walletId) {
        return List.of();
    }
}
