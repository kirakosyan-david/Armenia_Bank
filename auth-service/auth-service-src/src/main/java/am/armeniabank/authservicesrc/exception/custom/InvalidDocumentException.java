package am.armeniabank.authservicesrc.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidDocumentException extends RuntimeException {

    public InvalidDocumentException(String message) {
        super(message);
    }

    public InvalidDocumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidDocumentException(Throwable cause) {
        super(cause);
    }

    public InvalidDocumentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InvalidDocumentException() {
    }
}
