package am.armeniabank.authservicesrc.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LogoutFailedException extends RuntimeException {

    public LogoutFailedException(String message) {
        super(message);
    }
    public LogoutFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public LogoutFailedException(Throwable cause) {
        super(cause);
    }

    public LogoutFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public LogoutFailedException() {
    }
}
