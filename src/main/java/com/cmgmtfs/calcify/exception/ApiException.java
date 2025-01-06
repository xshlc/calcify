package com.cmgmtfs.calcify.exception;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}
