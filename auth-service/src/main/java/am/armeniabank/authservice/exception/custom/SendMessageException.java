package am.armeniabank.authservice.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class SendMessageException extends RuntimeException {

    public SendMessageException() {
        super();
    }

    public SendMessageException(String message) {
        super(message);
    }

    public SendMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public SendMessageException(Throwable cause) {
        super(cause);
    }
}