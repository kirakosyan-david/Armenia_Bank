package am.armeniabank.authservicesrc.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class KeycloakUserNotFoundException extends KeycloakException {

    public KeycloakUserNotFoundException(String email) {
        super("User not found in Keycloak with email: " + email);
    }

    public KeycloakUserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeycloakUserNotFoundException(Throwable cause) {
        super(cause);
    }

    public KeycloakUserNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public KeycloakUserNotFoundException() {
    }



}
