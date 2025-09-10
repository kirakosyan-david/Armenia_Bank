package am.armeniabank.authservicesrc.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class KeycloakRoleNotFoundException extends KeycloakException {

    public KeycloakRoleNotFoundException(String roleName) {
        super("Failed to retrieve role from Keycloak: " + roleName);
    }

    public KeycloakRoleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeycloakRoleNotFoundException(Throwable cause) {
        super(cause);
    }

    public KeycloakRoleNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public KeycloakRoleNotFoundException() {
    }

}
