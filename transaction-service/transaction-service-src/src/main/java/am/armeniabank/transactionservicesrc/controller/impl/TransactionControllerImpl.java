package am.armeniabank.transactionservicesrc.controller.impl;

import am.armeniabank.transactionserviceapi.contract.TransactionController;
import am.armeniabank.transactionserviceapi.request.TransactionRequest;
import am.armeniabank.transactionserviceapi.response.TransactionResponse;
import am.armeniabank.transactionservicesrc.controller.BaseController;
import am.armeniabank.transactionservicesrc.util.SecurityUtils;
import am.armeniabank.transactionservicesrc.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TransactionControllerImpl extends BaseController implements TransactionController {

    private final TransactionService transactionService;

    @Override
    public ResponseEntity<TransactionResponse> createTransaction(TransactionRequest request) {
        log.info("Creating Transaction for fromWalletId={} with toWalletId={}", request.getFromWalletId(), request.getToWalletId());
        TransactionResponse transaction = transactionService.createTransaction(request);
        return respond(transaction, transaction.getId(), "CREATE_TRANSACTION", HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<TransactionResponse> completeTransaction(UUID transactionId) {
        log.info("Completing transaction id={}", transactionId);
        TransactionResponse completed = transactionService.completeTransaction(transactionId);
        return respond(completed, completed.getId(), "COMPLETE_TRANSACTION", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TransactionResponse> cancelTransaction(UUID transactionId) {
        String token = SecurityUtils.getCurrentToken();
        log.info("Canceling transaction id={}", transactionId);
        TransactionResponse canceled = transactionService.cancelTransaction(transactionId, token);
        return respond(canceled, canceled.getId(), "CANCEL_TRANSACTION", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TransactionResponse> failTransaction(UUID transactionId) {
        String token = SecurityUtils.getCurrentToken();
        log.info("Failing transaction id={}", transactionId);
        TransactionResponse failed = transactionService.failTransaction(transactionId, token);
        return respond(failed, failed.getId(), "FAIL_TRANSACTION", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TransactionResponse> getTransactionById(UUID transactionId) {
        log.info("Fetching transaction by id={}", transactionId);
        TransactionResponse transaction = transactionService.getTransactionById(transactionId);
        return respond(transaction, transactionId, "GET_TRANSACTION", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<TransactionResponse>> getTransactionsByWallet(UUID walletId) {
        log.info("Fetching transactions for wallet id={}", walletId);
        List<TransactionResponse> transactions = transactionService.getTransactionsByWallet(walletId);
        return respond(transactions, walletId, "GET_WALLET_TRANSACTIONS", HttpStatus.OK);
    }
}
