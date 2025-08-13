package am.armeniabank.authservicesrc.exception;

import am.armeniabank.authserviceapi.response.RestErrorResponse;
import am.armeniabank.authservicesrc.exception.custom.ChangePasswordException;
import am.armeniabank.authservicesrc.exception.custom.DeleteNotFoundException;
import am.armeniabank.authservicesrc.exception.custom.EmailAlreadyExistsException;
import am.armeniabank.authservicesrc.exception.custom.SendMessageException;
import am.armeniabank.authservicesrc.exception.custom.UserServerError;
import am.armeniabank.authservicesrc.exception.custom.WrongUserIdException;
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


    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<String> handleEmailExists(EmailAlreadyExistsException e) {
        log.warn("Registration failed: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(value = {
            DeleteNotFoundException.class,
            HttpClientErrorException.class,
    })
    public ResponseEntity<Object> handleEntityNotFoundException(Exception ex, WebRequest request) {
        RestErrorResponse errorDto = RestErrorResponse.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .errorMessage(ex.getMessage())
                .build();
        return handleExceptionInternal(ex, errorDto, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(UserServerError.class)
    public ResponseEntity<String> handleException(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
    }

    @Override
    @ExceptionHandler(value = {
            WrongUserIdException.class,
            SendMessageException.class,
            ChangePasswordException.class
    })
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
        for (ObjectError error : allErrors) {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        }
        return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
