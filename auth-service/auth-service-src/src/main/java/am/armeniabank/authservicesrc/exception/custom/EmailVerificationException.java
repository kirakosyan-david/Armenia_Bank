package am.armeniabank.authservicesrc.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class EmailVerificationException extends RuntimeException {

    public EmailVerificationException() {
    }

    public EmailVerificationException(String message) {
        super(message);
    }

    public EmailVerificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailVerificationException(Throwable cause) {
        super(cause);
    }

    public EmailVerificationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}