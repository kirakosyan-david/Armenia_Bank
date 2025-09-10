package am.armeniabank.authservicesrc.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class TokenRefreshException extends RuntimeException {

    public TokenRefreshException(String message) {
        super(message);
    }
    public TokenRefreshException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenRefreshException(Throwable cause) {
        super(cause);
    }

    public TokenRefreshException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public TokenRefreshException() {
    }
}
