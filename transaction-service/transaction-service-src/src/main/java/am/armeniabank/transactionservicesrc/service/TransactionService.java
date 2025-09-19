package am.armeniabank.transactionservicesrc.service;

import am.armeniabank.transactionserviceapi.request.TransactionRequest;
import am.armeniabank.transactionserviceapi.response.TransactionResponse;
import am.armeniabank.transactionservicesrc.entity.Transaction;
import java.util.List;
import java.util.UUID;

public interface TransactionService {

    TransactionResponse createTransaction(TransactionRequest request);

    TransactionResponse completeTransaction(UUID transactionId, String authToken);

    TransactionResponse cancelTransaction(UUID transactionId, String reason);

    TransactionResponse failTransaction(UUID transactionId, String errorMessage);

    TransactionResponse getTransactionById(UUID transactionId);

    List<TransactionResponse> getTransactionsByWallet(UUID walletId);
}
