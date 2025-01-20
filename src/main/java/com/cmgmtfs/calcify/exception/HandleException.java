package com.cmgmtfs.calcify.exception;

import com.cmgmtfs.calcify.domain.HttpResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
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

}
