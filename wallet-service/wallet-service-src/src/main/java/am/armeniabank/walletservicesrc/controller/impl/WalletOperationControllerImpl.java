package am.armeniabank.walletservicesrc.controller.impl;

import am.armeniabank.walletserviceapi.contract.WalletOperationController;
import am.armeniabank.walletserviceapi.request.WalletOperationRequest;
import am.armeniabank.walletserviceapi.response.WalletOperationResponse;
import am.armeniabank.walletservicesrc.controller.BaseController;
import am.armeniabank.walletservicesrc.service.WalletOperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WalletOperationControllerImpl extends BaseController implements WalletOperationController {

    private final WalletOperationService walletOperationService;

    @Override
    public ResponseEntity<WalletOperationResponse> credit(UUID walletId, WalletOperationRequest reason) {
        log.info("Received CREDIT request for walletId={} amount={}", walletId, reason.getAmount());
        WalletOperationResponse credit = walletOperationService.credit(walletId, reason);
        return respond(credit, walletId, "CREDIT", HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<WalletOperationResponse> debit(UUID walletId, WalletOperationRequest reason) {
        log.info("Received DEBIT request for walletId={} amount={}", walletId, reason.getAmount());
        WalletOperationResponse debit = walletOperationService.debit(walletId, reason);
        return respond(debit, walletId, "DEBIT", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<WalletOperationResponse> freeze(UUID walletId, WalletOperationRequest reason) {
        log.info("Received FREEZE request for walletId={} amount={}", walletId, reason.getAmount());
        WalletOperationResponse freeze = walletOperationService.freeze(walletId, reason);
        return respond(freeze, walletId, "FREEZE", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<WalletOperationResponse> unfreeze(UUID walletId, WalletOperationRequest reason) {
        log.info("Received UNFREEZE request for walletId={} amount={}", walletId, reason.getAmount());
        WalletOperationResponse unfreeze = walletOperationService.unfreeze(walletId, reason);
        return respond(unfreeze, walletId, "UNFREEZE", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<WalletOperationResponse>> getOperations(UUID walletId) {
        log.info("Received GET OPERATIONS request for walletId={}", walletId);
        List<WalletOperationResponse> operations = walletOperationService.getOperations(walletId);
        log.info("Retrieved {} operations for walletId={}", operations.size(), walletId);
        return ResponseEntity.ok(operations);
    }
}
