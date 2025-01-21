package com.cmgmtfs.calcify.exception;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.cmgmtfs.calcify.domain.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
@Slf4j  // for logging
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


        List<FieldError> fieldErrors = exception.getBindingResult()
                .getFieldErrors();
        String fieldMessage = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
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
                .reason(exception.getMessage()
                        .contains("Duplicate entry") ? "Information already exists" : exception.getMessage())
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
        return new ResponseEntity<>(HttpResponse.builder()
                .timeStamp(now().toString())
                .reason(exception.getMessage())
                .developerMessage(exception.getMessage())  // don't do this in production
                .status(BAD_REQUEST)
                .statusCode(BAD_REQUEST.value())
                .build(), BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> accessDeniedException(AccessDeniedException exception) {
        return new ResponseEntity<>(HttpResponse.builder()
                .timeStamp(now().toString())
                .reason("Access denied. You don\'t have access to this resource.")
                .developerMessage(exception.getMessage())  // don't do this in production
                .status(FORBIDDEN)
                .statusCode(FORBIDDEN.value())
                .build(), FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> exception(Exception exception) {
        System.out.println(exception);
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .reason(exception.getMessage() != null ?
                                (exception.getMessage()
                                        .contains("expected 1, actual 0") ? "Record not found" : exception.getMessage())
                                : "Some error occurred")
                        .developerMessage(exception.getMessage())
                        .status(INTERNAL_SERVER_ERROR)
                        .statusCode(INTERNAL_SERVER_ERROR.value())
                        .build(), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(JWTDecodeException.class)
    public ResponseEntity<HttpResponse> exception(JWTDecodeException exception) {
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .reason("Could not decode the token")
                        .developerMessage(exception.getMessage())
                        .status(INTERNAL_SERVER_ERROR)
                        .statusCode(INTERNAL_SERVER_ERROR.value())
                        .build(), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<HttpResponse> emptyResultDataAccessException(EmptyResultDataAccessException exception) {
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .reason(exception.getMessage()
                                .contains("expected 1, actual 0") ? "Record not found" : exception.getMessage())
                        .developerMessage(exception.getMessage())
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value())
                        .build(), BAD_REQUEST);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HttpResponse> disabledException(DisabledException exception) {
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .developerMessage(exception.getMessage())
                        //.reason(exception.getMessage() + ". Please check your email and verify your account.")
                        .reason("User account is currently disabled")
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value())
                        .build()
                , BAD_REQUEST);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HttpResponse> lockedException(LockedException exception) {
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .developerMessage(exception.getMessage())
                        //.reason(exception.getMessage() + ", too many failed attempts.")
                        .reason("User account is currently locked")
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value())
                        .build()
                , BAD_REQUEST);
    }


}
