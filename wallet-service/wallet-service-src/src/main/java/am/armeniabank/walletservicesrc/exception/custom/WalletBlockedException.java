package am.armeniabank.walletservicesrc.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class WalletBlockedException extends RuntimeException {

    public WalletBlockedException(UUID walletId) {
        super("Wallet is blocked and cannot be used: " + walletId);
    }

    public WalletBlockedException() {
    }

    public WalletBlockedException(String message) {
        super(message);
    }

    public WalletBlockedException(String message, Throwable cause) {
        super(message, cause);
    }

    public WalletBlockedException(Throwable cause) {
        super(cause);
    }

    public WalletBlockedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
