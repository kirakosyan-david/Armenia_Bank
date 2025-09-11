package am.armeniabank.walletservicesrc.handler;


import am.armeniabank.walletserviceapi.enums.WalletOperationType;
import am.armeniabank.walletserviceapi.request.WalletOperationRequest;

import java.util.UUID;

public interface WalletEventHandler {

    boolean isHandle(WalletOperationType walletOperationType);

    void handle(UUID walletId, WalletOperationRequest request);
}
