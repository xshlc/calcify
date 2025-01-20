package com.cmgmtfs.calcify.exception;

import com.cmgmtfs.calcify.domain.HttpResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static java.time.LocalDateTime.now;
import static java.util.Map.of;
import static org.springframework.http.HttpStatus.CREATED;
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
}
