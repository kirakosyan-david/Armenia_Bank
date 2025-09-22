package am.armeniabank.transactionservicesrc.controller;

import am.armeniabank.transactionserviceapi.response.ListResponse;
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

    protected <T> ResponseEntity<ListResponse<T>> respond(List<T> body, String operation, HttpStatus status, String emptyMessage) {
        if (body == null || body.isEmpty()) {
            log.info("{} operation: {}", operation, emptyMessage);
            return new ResponseEntity<>(new ListResponse<>(List.of(), emptyMessage), status);
        }
        log.info("{} operation completed successfully, list size={}", operation, body.size());
        return new ResponseEntity<>(new ListResponse<>(body, null), status);
    }

}
