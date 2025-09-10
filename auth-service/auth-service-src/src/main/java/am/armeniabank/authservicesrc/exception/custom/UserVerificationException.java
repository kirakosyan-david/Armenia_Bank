package am.armeniabank.authservicesrc.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class UserVerificationException extends RuntimeException {

    public UserVerificationException(String message) {
        super(message);
    }

    public UserVerificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserVerificationException(Throwable cause) {
        super(cause);
    }

    public UserVerificationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public UserVerificationException() {
    }
}
