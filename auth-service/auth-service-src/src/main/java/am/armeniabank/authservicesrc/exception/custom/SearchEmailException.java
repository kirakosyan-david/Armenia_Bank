package am.armeniabank.authservicesrc.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class SearchEmailException extends RuntimeException {

    public SearchEmailException() {
        super();
    }

    public SearchEmailException(String message) {
        super(message);
    }

    public SearchEmailException(String message, Throwable cause) {
        super(message, cause);
    }

    public SearchEmailException(Throwable cause) {
        super(cause);
    }
}