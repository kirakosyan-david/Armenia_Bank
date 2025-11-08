package am.armeniabank.walletservicesrc.exception;

import am.armeniabank.armeniabankcommon.excepition.UserNotFoundException;
import am.armeniabank.armeniabankcommon.response.RestErrorResponse;
import am.armeniabank.walletservicesrc.exception.custom.*;
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
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<Object> handleWalletNotFound(WalletNotFoundException e, WebRequest request) {
        log.warn("Wallet not found: {}", e.getMessage());
        RestErrorResponse error = RestErrorResponse.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .errorMessage(e.getMessage())
                .build();
        return handleExceptionInternal(e, error, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFound(UserNotFoundException e, WebRequest request) {
        log.warn("User not found: {}", e.getMessage());
        RestErrorResponse error = RestErrorResponse.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .errorMessage(e.getMessage())
                .build();
        return handleExceptionInternal(e, error, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(WalletBlockedException.class)
    public ResponseEntity<Object> handleWalletBlocked(WalletBlockedException e, WebRequest request) {
        log.warn("Wallet is blocked: {}", e.getMessage());
        RestErrorResponse error = RestErrorResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .errorMessage(e.getMessage())
                .build();
        return handleExceptionInternal(e, error, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<Object> handleInvalidAmount(InvalidAmountException e, WebRequest request) {
        log.warn("Invalid amount: {}", e.getMessage());
        RestErrorResponse error = RestErrorResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .errorMessage(e.getMessage())
                .build();
        return handleExceptionInternal(e, error, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<Object> handleInsufficientBalance(InsufficientBalanceException e, WebRequest request) {
        log.warn("Insufficient balance: {}", e.getMessage());
        RestErrorResponse error = RestErrorResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .errorMessage(e.getMessage())
                .build();
        return handleExceptionInternal(e, error, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
        for (ObjectError error : allErrors) {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        }
        return handleExceptionInternal(ex, errors, headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllOtherExceptions(Exception ex, WebRequest request) {
        log.error("Unexpected error: ", ex);
        RestErrorResponse error = RestErrorResponse.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorMessage(ex.getMessage())
                .build();
        return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
