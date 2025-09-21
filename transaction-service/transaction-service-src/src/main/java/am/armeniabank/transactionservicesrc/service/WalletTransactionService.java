package am.armeniabank.transactionservicesrc.service;

import am.armeniabank.transactionserviceapi.request.TransactionRequest;
import am.armeniabank.transactionservicesrc.entity.Transaction;

public interface WalletTransactionService {
    void freeze(TransactionRequest request, String token);

    void unfreeze(Transaction transaction, String token);

    void credit(Transaction transaction, String token);

    void debit(Transaction transaction, String token);
}
