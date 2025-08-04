package am.armeniabank.authservice.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class UserServerError extends RuntimeException {

    public UserServerError() {
        super();
    }

    public UserServerError(String message) {
        super(message);
    }

    public UserServerError(String message, Throwable cause) {
        super(message, cause);
    }

    public UserServerError(Throwable cause) {
        super(cause);
    }
}