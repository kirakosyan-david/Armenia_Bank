package am.armeniabank.authservicesrc.exception;

import am.armeniabank.authserviceapi.response.RestErrorResponse;
import am.armeniabank.authservicesrc.exception.custom.ChangePasswordException;
import am.armeniabank.authservicesrc.exception.custom.DeleteNotFoundException;
import am.armeniabank.authservicesrc.exception.custom.EmailAlreadyExistsException;
import am.armeniabank.authservicesrc.exception.custom.EmailVerificationException;
import am.armeniabank.authservicesrc.exception.custom.InvalidDocumentException;
import am.armeniabank.authservicesrc.exception.custom.KeycloakAccessTokenException;
import am.armeniabank.authservicesrc.exception.custom.KeycloakDeleteException;
import am.armeniabank.authservicesrc.exception.custom.KeycloakException;
import am.armeniabank.authservicesrc.exception.custom.KeycloakRoleNotFoundException;
import am.armeniabank.authservicesrc.exception.custom.KeycloakUpdateException;
import am.armeniabank.authservicesrc.exception.custom.KeycloakUserCreationException;
import am.armeniabank.authservicesrc.exception.custom.KeycloakUserNotFoundException;
import am.armeniabank.authservicesrc.exception.custom.LoginFailedException;
import am.armeniabank.authservicesrc.exception.custom.LogoutFailedException;
import am.armeniabank.authservicesrc.exception.custom.SendMessageException;
import am.armeniabank.authservicesrc.exception.custom.TokenRefreshException;
import am.armeniabank.authservicesrc.exception.custom.UserLoginException;
import am.armeniabank.authservicesrc.exception.custom.UserNotAuthenticatedException;
import am.armeniabank.authservicesrc.exception.custom.UserNotFoundException;
import am.armeniabank.authservicesrc.exception.custom.UserProfileException;
import am.armeniabank.authservicesrc.exception.custom.UserProfileNotFoundException;
import am.armeniabank.authservicesrc.exception.custom.UserServerError;
import am.armeniabank.authservicesrc.exception.custom.UserVerificationException;
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

    @ExceptionHandler({
            DeleteNotFoundException.class,
            UserNotFoundException.class,
            UserProfileNotFoundException.class,
            KeycloakUserNotFoundException.class,
            KeycloakRoleNotFoundException.class,
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
            LoginFailedException.class,
            TokenRefreshException.class,
            UserLoginException.class,
            KeycloakAccessTokenException.class,
            UserNotAuthenticatedException.class
    })
    public ResponseEntity<Object> handleUnauthorizedExceptions(RuntimeException ex, WebRequest request) {
        log.warn("Unauthorized access: {}", ex.getMessage());
        RestErrorResponse errorDto = RestErrorResponse.builder()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .errorMessage(ex.getMessage())
                .build();
        return handleExceptionInternal(ex, errorDto, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler({
            UserServerError.class,
            UserProfileException.class,
            KeycloakException.class,
            KeycloakDeleteException.class,
            KeycloakUpdateException.class,
            KeycloakUserCreationException.class,
            EmailVerificationException.class,
            UserVerificationException.class
    })
    public ResponseEntity<Object> handleInternalServerError(RuntimeException ex, WebRequest request) {
        log.error("Internal server error: {}", ex.getMessage(), ex);
        RestErrorResponse errorDto = RestErrorResponse.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorMessage(ex.getMessage())
                .build();
        return handleExceptionInternal(ex, errorDto, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler({
            WrongUserIdException.class,
            SendMessageException.class,
            ChangePasswordException.class,
            LogoutFailedException.class,
            InvalidDocumentException.class
    })
    public ResponseEntity<Object> handleBadRequestExceptions(RuntimeException ex, WebRequest request) {
        log.warn("Bad request: {}", ex.getMessage(), ex);
        RestErrorResponse errorDto = RestErrorResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .errorMessage(ex.getMessage())
                .build();
        return handleExceptionInternal(ex, errorDto, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
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
