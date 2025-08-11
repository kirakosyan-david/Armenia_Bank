package am.armeniabank.authservicesrc.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class WrongUserIdException extends RuntimeException {

    public WrongUserIdException() {
        super();
    }

    public WrongUserIdException(String message) {
        super(message);
    }

    public WrongUserIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongUserIdException(Throwable cause) {
        super(cause);
    }
}