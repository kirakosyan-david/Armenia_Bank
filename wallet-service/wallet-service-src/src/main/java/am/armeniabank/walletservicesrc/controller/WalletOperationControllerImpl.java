package am.armeniabank.walletservicesrc.controller;

import am.armeniabank.walletserviceapi.contract.WalletOperationController;
import am.armeniabank.walletserviceapi.request.WalletOperationRequest;
import am.armeniabank.walletserviceapi.response.WalletOperationResponse;
import am.armeniabank.walletservicesrc.service.WalletOperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class WalletOperationControllerImpl implements WalletOperationController {

    private final WalletOperationService walletOperationService;

    @Override
    public ResponseEntity<WalletOperationResponse> credit(UUID walletId, WalletOperationRequest reason) {
        WalletOperationResponse credit = walletOperationService.credit(walletId, reason);
        return ResponseEntity.ok(credit);
    }

    @Override
    public ResponseEntity<WalletOperationResponse> debit(UUID walletId, WalletOperationRequest reason) {
        WalletOperationResponse debit = walletOperationService.debit(walletId, reason);
        return ResponseEntity.ok(debit);
    }

    @Override
    public ResponseEntity<WalletOperationResponse> freeze(UUID walletId, WalletOperationRequest reason) {
        WalletOperationResponse freeze = walletOperationService.freeze(walletId, reason);
        return ResponseEntity.ok(freeze);
    }

    @Override
    public ResponseEntity<WalletOperationResponse> unfreeze(UUID walletId, WalletOperationRequest reason) {
        WalletOperationResponse unfreeze = walletOperationService.unfreeze(walletId, reason);
        return ResponseEntity.ok(unfreeze);
    }

    @Override
    public ResponseEntity<List<WalletOperationResponse>> getOperations(UUID walletId) {
        List<WalletOperationResponse> operations = walletOperationService.getOperations(walletId);
        return ResponseEntity.ok(operations);
    }
}
