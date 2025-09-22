package am.armeniabank.transactionservicesrc.exception.custam;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class FreezeNotFoundException extends RuntimeException {

    public FreezeNotFoundException() {
        super();
    }

    public FreezeNotFoundException(String message) {
        super(message);
    }

    public FreezeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public FreezeNotFoundException(Throwable cause) {
        super(cause);
    }

    protected FreezeNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
