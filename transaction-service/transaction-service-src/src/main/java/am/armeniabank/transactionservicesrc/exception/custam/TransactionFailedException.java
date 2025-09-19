package am.armeniabank.transactionservicesrc.exception.custam;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FAILED_DEPENDENCY)
public class TransactionFailedException extends RuntimeException {

    public TransactionFailedException() {
    }

    public TransactionFailedException(String message) {
        super(message);
    }

    public TransactionFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionFailedException(Throwable cause) {
        super(cause);
    }

    public TransactionFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
