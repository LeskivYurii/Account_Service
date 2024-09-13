package account.exception;

import account.exception.custom.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Objects;

@ControllerAdvice
public class AccountExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorMessage> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                        WebRequest webRequest) throws URISyntaxException {
        return ResponseEntity.status(400).body(new ErrorMessage(LocalDate.now(), ex.getBody().getStatus(),
                "Bad Request", new URI(((ServletWebRequest) webRequest).getRequest().getRequestURI()).getPath(),
                Objects.requireNonNull(ex.getFieldError()).getDefaultMessage()));
    }

    @ExceptionHandler(value = {ChangePasswordException.class, PayrollException.class, ConstraintViolationException.class,
            RemoveAdminUserException.class, ChangeRoleException.class, ChangeUserAccessException.class})
    public ResponseEntity<ErrorMessage> handleBadRequestExceptions(RuntimeException runtimeException,
                                                                      WebRequest webRequest) throws URISyntaxException {
        return ResponseEntity
                .status(400)
                .body(toErrorMessage("Bad Request", 400, runtimeException, webRequest));

    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorMessage> handleRuntimeException(RuntimeException runtimeException,
                                                                      WebRequest webRequest) throws URISyntaxException {
        return ResponseEntity
                .status(400)
                .body(toErrorMessage("Bad Request", 400, runtimeException, webRequest));

    }

    @ExceptionHandler({UserNotFoundException.class, RoleNotFoundException.class})
    public ResponseEntity<ErrorMessage> handleNotFoundException(RuntimeException runtimeException,
                                                                      WebRequest webRequest) throws URISyntaxException {
        return ResponseEntity
                .status(404)
                .body(toErrorMessage("Not Found", 404, runtimeException, webRequest));

    }

    private ErrorMessage toErrorMessage(String error, int code, RuntimeException ex, WebRequest webRequest)
                                                                                            throws URISyntaxException {
        return new ErrorMessage(LocalDate.now(), code, error
                , new URI(((ServletWebRequest) webRequest).getRequest().getRequestURI()).getPath(),
                ex.getMessage());
    }

}
