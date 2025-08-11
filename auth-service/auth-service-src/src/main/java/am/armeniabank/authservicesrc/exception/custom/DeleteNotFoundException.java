package am.armeniabank.authservicesrc.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class DeleteNotFoundException extends RuntimeException {

    public DeleteNotFoundException() {
        super();
    }

    public DeleteNotFoundException(String message) {
        super(message);
    }

    public DeleteNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeleteNotFoundException(Throwable cause) {
        super(cause);
    }
}