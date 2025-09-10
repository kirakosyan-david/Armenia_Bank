package am.armeniabank.authservicesrc.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class KeycloakDeleteException extends RuntimeException {

    public KeycloakDeleteException() {
    }

    public KeycloakDeleteException(String message) {
        super(message);
    }

    public KeycloakDeleteException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeycloakDeleteException(Throwable cause) {
        super(cause);
    }

    public KeycloakDeleteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
