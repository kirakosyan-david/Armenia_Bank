package am.armeniabank.transactionservicesrc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Slf4j
public abstract class BaseController {

    protected <T> ResponseEntity<T> respond(T body, UUID entityId, String operation, HttpStatus status) {
        if (body != null) {
            log.info("{} operation completed successfully for id={}", operation, entityId);
            return new ResponseEntity<>(body, status);
        } else {
            log.warn("{} operation returned null response for id={}", operation, entityId);
            return new ResponseEntity<>(status);
        }
    }

    protected <T> ResponseEntity<List<T>> respond(List<T> body, UUID entityId, String operation, HttpStatus status) {
        if (body != null) {
            log.info("{} operation completed successfully for id={}, list size={}", operation, entityId, body.size());
            return new ResponseEntity<>(body, status);
        } else {
            log.warn("{} operation returned null list for id={}", operation, entityId);
            return new ResponseEntity<>(status);
        }
    }
}
