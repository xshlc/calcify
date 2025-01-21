package com.cmgmtfs.calcify.exception;

import com.cmgmtfs.calcify.domain.HttpResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.resolve;

public class HandleException extends ResponseEntityExceptionHandler implements ErrorController {
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception exception, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
//        return super.handleExceptionInternal(exception, body, headers, statusCode, request);
        return new ResponseEntity<>(HttpResponse.builder()
                .timeStamp(now().toString())
                .reason(exception.getMessage())
                .developerMessage(exception.getMessage())
                .status(resolve(statusCode.value()))
                .statusCode(statusCode.value())
                .build(), statusCode);

    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
//        return super.handleMethodArgumentNotValid(ex, headers, status, request);

        /*

        public class LoginForm {
            @NotEmpty(message = "Email cannot be empty")
            @Email(message = "Invalid email. Please enter a valid email address.")
            private String email;
            @NotEmpty(message = "Password cannot be empty")
            private String password;
        }

        the fieldErrors are for the @NotEmpty and such

         */



        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        String fieldMessage = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));
        return new ResponseEntity<>(HttpResponse.builder()
                .timeStamp(now().toString())
//                .reason(exception.getMessage())
                .reason(fieldMessage)
                .developerMessage(exception.getMessage())  // don't do this in production
                .status(resolve(statusCode.value()))
                .statusCode(statusCode.value())
                .build(), statusCode);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<HttpResponse> sqlIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException exception) {


        return new ResponseEntity<>(HttpResponse.builder()
                .timeStamp(now().toString())
                // what we are showing through the reason() is not good in production
                // only okay for developing and testing
                .reason(exception.getMessage().contains("Duplicate entry") ? "Information already exists" : exception.getMessage())
                .developerMessage(exception.getMessage())  // don't do this in production
                .status(BAD_REQUEST)
                .statusCode(BAD_REQUEST.value())
                .build(), BAD_REQUEST);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<HttpResponse> generalExceptionHandler(Exception exception) {
//        // this method will catch every exception
//        return new ResponseEntity<>(HttpResponse.builder()
//                .timeStamp(now().toString())
//                .reason(exception.getMessage())
//                .developerMessage(exception.getMessage())  // don't do this in production
//                .status(BAD_REQUEST)
//                .statusCode(BAD_REQUEST.value())
//                .build(), BAD_REQUEST);
//    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> badCredentialException(BadCredentialsException exception) {
        // this method will catch every exception
        return new ResponseEntity<>(HttpResponse.builder()
                .timeStamp(now().toString())
                .reason(exception.getMessage() + ", Incorrect email or password ")
                .developerMessage(exception.getMessage())  // don't do this in production
                .status(BAD_REQUEST)
                .statusCode(BAD_REQUEST.value())
                .build(), BAD_REQUEST);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<HttpResponse> apiException(ApiException exception) {
        // this method will catch every exception
        return new ResponseEntity<>(HttpResponse.builder()
                .timeStamp(now().toString())
                .reason(exception.getMessage())
                .developerMessage(exception.getMessage())  // don't do this in production
                .status(BAD_REQUEST)
                .statusCode(BAD_REQUEST.value())
                .build(), BAD_REQUEST);
    }

}
