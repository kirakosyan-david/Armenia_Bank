package am.armeniabank.transactionservicesrc.exception.custam;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class FreezeOperationException extends RuntimeException {

    public FreezeOperationException() {
        super();
    }

    public FreezeOperationException(String message) {
        super(message);
    }

    public FreezeOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public FreezeOperationException(Throwable cause) {
        super(cause);
    }

    protected FreezeOperationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
