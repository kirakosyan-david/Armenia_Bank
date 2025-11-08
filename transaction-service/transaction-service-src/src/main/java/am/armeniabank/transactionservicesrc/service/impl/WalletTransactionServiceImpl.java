package am.armeniabank.transactionservicesrc.service.impl;

import am.armeniabank.armeniabankcommon.contract.WalletApi;
import am.armeniabank.armeniabankcommon.enums.WalletOperationType;
import am.armeniabank.armeniabankcommon.request.WalletOperationRequest;
import am.armeniabank.transactionserviceapi.request.TransactionRequest;
import am.armeniabank.transactionservicesrc.entity.Transaction;
import am.armeniabank.transactionservicesrc.service.WalletTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletTransactionServiceImpl implements WalletTransactionService {

    private final WalletApi walletApi;

    @Override
    public void freeze(TransactionRequest request, String token) {
        WalletOperationRequest freezeRequest = WalletOperationRequest.builder()
                .walletOperationType(WalletOperationType.FREEZE)
                .amount(request.getAmount())
                .build();

        walletApi.freezeWallet(request.getFromWalletId(), freezeRequest, "Bearer " + token);
    }

    @Override
    public void unfreeze(Transaction transaction, String token) {
        WalletOperationRequest unfreezeRequest = WalletOperationRequest.builder()
                .walletOperationType(WalletOperationType.UNFREEZE)
                .amount(transaction.getAmount())
                .build();

        walletApi.unfreezeWallet(transaction.getFromWalletId(), unfreezeRequest, "Bearer " + token);
    }

    @Override
    public void credit(Transaction transaction, String token) {
        WalletOperationRequest creditRequest = WalletOperationRequest.builder()
                .walletOperationType(WalletOperationType.CREDIT)
                .amount(transaction.getAmount())
                .build();

        walletApi.creditWallet(transaction.getToWalletId(), creditRequest, "Bearer " + token);
    }

    @Override
    public void debit(Transaction transaction, String token) {
        WalletOperationRequest debitRequest = WalletOperationRequest.builder()
                .walletOperationType(WalletOperationType.DEBIT)
                .amount(transaction.getAmount())
                .build();
        walletApi.debitWallet(transaction.getFromWalletId(), debitRequest, "Bearer " + token);
    }
}
