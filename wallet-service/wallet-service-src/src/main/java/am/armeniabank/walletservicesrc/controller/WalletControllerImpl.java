package am.armeniabank.walletservicesrc.controller;

import am.armeniabank.walletserviceapi.contract.WalletController;
import am.armeniabank.walletserviceapi.enums.Currency;
import am.armeniabank.walletserviceapi.response.WalletResponse;
import am.armeniabank.walletservicesrc.service.WalletService;
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
public class WalletControllerImpl implements WalletController {

    private final WalletService walletService;

    @Override
    public ResponseEntity<WalletResponse> createWallet(UUID userId, Currency currency) {
        log.info("Creating wallet for userId={} with currency={}", userId, currency);

        WalletResponse wallet = walletService.createWallet(userId, currency);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(wallet);
    }

    @Override
    public ResponseEntity<WalletResponse> getWalletById(UUID walletId) {
        log.info("Fetching wallet by id={}", walletId);
        WalletResponse wallet = walletService.getWalletById(walletId);

        return ResponseEntity.ok(wallet);
    }

    @Override
    public ResponseEntity<List<WalletResponse>> getWalletsByUserId(UUID userId) {
        log.info("Fetching wallets for userId={}", userId);
        List<WalletResponse> walletsByUserId = walletService.getWalletsByUserId(userId);
        return ResponseEntity.ok(walletsByUserId);
    }

    @Override
    public ResponseEntity<WalletResponse> blockWallet(UUID walletId) {
        log.info("Blocking wallet id={}", walletId);
        WalletResponse walletResponse = walletService.blockWallet(walletId);
        return ResponseEntity.ok(walletResponse);
    }

    @Override
    public ResponseEntity<WalletResponse> unblockWallet(UUID walletId) {
        log.info("Unblocking wallet id={}", walletId);
        WalletResponse walletResponse = walletService.unblockWallet(walletId);
        return ResponseEntity.ok(walletResponse);
    }

    @Override
    public ResponseEntity<WalletResponse> closeWallet(UUID walletId) {
        log.info("Closing wallet id={}", walletId);
        WalletResponse walletResponse = walletService.closeWallet(walletId);
        return ResponseEntity.ok(walletResponse);
    }
}
