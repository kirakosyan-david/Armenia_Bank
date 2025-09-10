package am.armeniabank.authservicesrc.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class UserProfileException extends RuntimeException {

    public UserProfileException() {
    }

    public UserProfileException(String message) {
        super(message);
    }

    public UserProfileException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserProfileException(Throwable cause) {
        super(cause);
    }

    public UserProfileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
