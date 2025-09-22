package am.armeniabank.transactionservicesrc.exception.custam;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidFreezeStateException extends RuntimeException {

    public InvalidFreezeStateException() {
        super();
    }

    public InvalidFreezeStateException(String message) {
        super(message);
    }

    public InvalidFreezeStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidFreezeStateException(Throwable cause) {
        super(cause);
    }

    protected InvalidFreezeStateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
