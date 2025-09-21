package am.armeniabank.walletservicesrc.controller.impl;

import am.armeniabank.walletserviceapi.contract.WalletController;
import am.armeniabank.walletserviceapi.enums.Currency;
import am.armeniabank.walletserviceapi.response.WalletResponse;
import am.armeniabank.walletservicesrc.controller.BaseController;
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
public class WalletControllerImpl extends BaseController implements WalletController {

    private final WalletService walletService;

    @Override
    public ResponseEntity<WalletResponse> createWallet(Currency currency) {
        WalletResponse wallet = walletService.createWallet(currency);
        log.info("Creating wallet for userId={} with currency={}", wallet.getUserId(), currency);
        return respond(wallet, wallet.getUserId(), "CREATE_WALLET", HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<WalletResponse> getWalletById(UUID walletId) {
        log.info("Fetching wallet by id={}", walletId);
        WalletResponse wallet = walletService.getWalletById(walletId);
        return respond(wallet, walletId, "GET_WALLET", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<WalletResponse>> getWalletsByUserId(UUID userId) {
        log.info("Fetching wallets for userId={}", userId);
        List<WalletResponse> walletsByUserId = walletService.getWalletsByUserId(userId);
        return respond(walletsByUserId, userId, "GET_WALLETS_BY_USER", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<WalletResponse> blockWallet(UUID walletId) {
        log.info("Blocking wallet id={}", walletId);
        WalletResponse walletResponse = walletService.blockWallet(walletId);
        return respond(walletResponse, walletId, "BLOCK_WALLET", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<WalletResponse> unblockWallet(UUID walletId) {
        log.info("Unblocking wallet id={}", walletId);
        WalletResponse walletResponse = walletService.unblockWallet(walletId);
        return respond(walletResponse, walletId, "UNBLOCK_WALLET", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<WalletResponse> closeWallet(UUID walletId) {
        log.info("Closing wallet id={}", walletId);
        WalletResponse walletResponse = walletService.closeWallet(walletId);
        return respond(walletResponse, walletId, "CLOSE_WALLET", HttpStatus.OK);
    }
}
