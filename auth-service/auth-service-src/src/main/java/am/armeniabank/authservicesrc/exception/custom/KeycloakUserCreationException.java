package am.armeniabank.authservicesrc.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class KeycloakUserCreationException extends KeycloakException {

    public KeycloakUserCreationException(String message) {
        super("Failed to create Keycloak user: " + message);
    }

    public KeycloakUserCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeycloakUserCreationException(Throwable cause) {
        super(cause);
    }

    public KeycloakUserCreationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public KeycloakUserCreationException() {
    }

}
