package am.armeniabank.transactionservicesrc.exception;

import am.armeniabank.armeniabankcommon.excepition.InsufficientFundsException;
import am.armeniabank.armeniabankcommon.excepition.UserNotFoundException;
import am.armeniabank.armeniabankcommon.response.RestErrorResponse;
import am.armeniabank.transactionservicesrc.exception.custam.FreezeNotFoundException;
import am.armeniabank.transactionservicesrc.exception.custam.FreezeOperationException;
import am.armeniabank.transactionservicesrc.exception.custam.InvalidFreezeStateException;
import am.armeniabank.transactionservicesrc.exception.custam.TransactionFailedException;
import am.armeniabank.transactionservicesrc.exception.custam.TransactionNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler({
            TransactionNotFoundException.class,
            UserNotFoundException.class,
            FreezeNotFoundException.class,
            HttpClientErrorException.class
    })
    public ResponseEntity<Object> handleNotFoundExceptions(Exception ex, WebRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        RestErrorResponse errorDto = RestErrorResponse.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .errorMessage(ex.getMessage())
                .build();
        return handleExceptionInternal(ex, errorDto, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({
            InsufficientFundsException.class,
    })
    public ResponseEntity<Object> handleUnprocessableEntityExceptions(RuntimeException ex, WebRequest request) {
        log.warn("Unprocessable Entity: {}", ex.getMessage(), ex);
        RestErrorResponse errorDto = RestErrorResponse.builder()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .errorMessage(ex.getMessage())
                .build();
        return handleExceptionInternal(ex, errorDto, new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @ExceptionHandler({
            TransactionFailedException.class,
    })
    public ResponseEntity<Object> handleFailedDependencyExceptions(RuntimeException ex, WebRequest request) {
        log.warn("Failed Dependency: {}", ex.getMessage(), ex);
        RestErrorResponse errorDto = RestErrorResponse.builder()
                .statusCode(HttpStatus.FAILED_DEPENDENCY.value())
                .errorMessage(ex.getMessage())
                .build();
        return handleExceptionInternal(ex, errorDto, new HttpHeaders(), HttpStatus.FAILED_DEPENDENCY, request);
    }

    @ExceptionHandler({
            InvalidFreezeStateException.class
    })
    public ResponseEntity<Object> handleBadRequestExceptions(RuntimeException ex, WebRequest request) {
        log.warn("Bad request: {}", ex.getMessage(), ex);
        RestErrorResponse errorDto = RestErrorResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .errorMessage(ex.getMessage())
                .build();
        return handleExceptionInternal(ex, errorDto, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({
            FreezeOperationException.class
    })
    public ResponseEntity<Object> handleInternalServerError(RuntimeException ex, WebRequest request) {
        log.error("Internal server error: {}", ex.getMessage(), ex);
        RestErrorResponse errorDto = RestErrorResponse.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorMessage(ex.getMessage())
                .build();
        return handleExceptionInternal(ex, errorDto, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllUnhandledExceptions(Exception ex, WebRequest request) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        RestErrorResponse errorDto = RestErrorResponse.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorMessage("Unexpected error: " + ex.getMessage())
                .build();
        return handleExceptionInternal(ex, errorDto, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
        for (ObjectError error : allErrors) {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        }
        log.warn("Validation failed: {}", errors);
        return handleExceptionInternal(ex, errors, headers, HttpStatus.BAD_REQUEST, request);
    }
}
