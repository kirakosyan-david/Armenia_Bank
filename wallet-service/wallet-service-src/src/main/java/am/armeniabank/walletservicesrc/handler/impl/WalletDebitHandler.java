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
public class WalletDebitHandler implements WalletEventHandler {

    private final WalletOperationService walletOperationService;

    @Override
    public boolean isHandle(WalletOperationType walletOperationType) {
        return walletOperationType == WalletOperationType.DEBIT;
    }

    @Override
    public void handle(UUID walletId, WalletOperationRequest request) {
        walletOperationService.debit(walletId, request);
    }
}
