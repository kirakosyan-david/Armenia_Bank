package am.armeniabank.walletservicesrc.handler.impl;

import am.armeniabank.armeniabankcommon.enums.WalletOperationType;
import am.armeniabank.armeniabankcommon.request.WalletOperationRequest;
import am.armeniabank.walletservicesrc.handler.WalletEventHandler;
import am.armeniabank.walletservicesrc.service.WalletOperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WalletFreezeHandler implements WalletEventHandler {

    private final WalletOperationService walletOperationService;

    @Override
    public boolean isHandle(WalletOperationType walletOperationType) {
        return walletOperationType == WalletOperationType.FREEZE;
    }

    @Override
    public void handle(UUID walletId, WalletOperationRequest request) {
        walletOperationService.freeze(walletId, request);
    }
}
