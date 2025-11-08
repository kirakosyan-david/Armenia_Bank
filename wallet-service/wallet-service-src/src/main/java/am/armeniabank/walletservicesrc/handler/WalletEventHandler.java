package am.armeniabank.walletservicesrc.handler;


import am.armeniabank.armeniabankcommon.enums.WalletOperationType;
import am.armeniabank.armeniabankcommon.request.WalletOperationRequest;

import java.util.UUID;

public interface WalletEventHandler {

    boolean isHandle(WalletOperationType walletOperationType);

    void handle(UUID walletId, WalletOperationRequest request);
}
