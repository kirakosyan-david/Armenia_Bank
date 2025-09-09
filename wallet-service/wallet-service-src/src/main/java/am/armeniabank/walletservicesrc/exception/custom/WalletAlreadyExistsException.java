package am.armeniabank.walletservicesrc.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class WalletAlreadyExistsException extends RuntimeException {

    public WalletAlreadyExistsException(UUID userId, String currency) {
        super("Wallet already exists for user: " + userId + " with currency: " + currency);
    }

    public WalletAlreadyExistsException() {
    }

    public WalletAlreadyExistsException(String message) {
        super(message);
    }

    public WalletAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public WalletAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    public WalletAlreadyExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
