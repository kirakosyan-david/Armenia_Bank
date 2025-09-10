package am.armeniabank.authservicesrc.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class KeycloakAccessTokenException extends RuntimeException {

    public KeycloakAccessTokenException() {
    }

    public KeycloakAccessTokenException(String message) {
        super(message);
    }

    public KeycloakAccessTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeycloakAccessTokenException(Throwable cause) {
        super(cause);
    }

    public KeycloakAccessTokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
