package am.armeniabank.walletservicesrc.handler.impl;

import am.armeniabank.walletserviceapi.enums.WalletOperationType;
import am.armeniabank.walletserviceapi.request.WalletOperationRequest;
import am.armeniabank.walletservicesrc.handler.WalletEventHandler;
import am.armeniabank.walletservicesrc.service.WalletOperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WalletCreditHandler implements WalletEventHandler {

    private final WalletOperationService walletOperationService;

    @Override
    public boolean isHandle(WalletOperationType walletOperationType) {
        return walletOperationType == WalletOperationType.CREDIT;
    }

    @Override
    public void handle(UUID walletId, WalletOperationRequest request) {
        walletOperationService.credit(walletId, request);
    }
}
