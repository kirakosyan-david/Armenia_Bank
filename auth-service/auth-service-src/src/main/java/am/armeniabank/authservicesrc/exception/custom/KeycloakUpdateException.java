package am.armeniabank.authservicesrc.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class KeycloakUpdateException extends RuntimeException {

    public KeycloakUpdateException() {
    }

    public KeycloakUpdateException(String message) {
        super(message);
    }

    public KeycloakUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeycloakUpdateException(Throwable cause) {
        super(cause);
    }

    public KeycloakUpdateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
