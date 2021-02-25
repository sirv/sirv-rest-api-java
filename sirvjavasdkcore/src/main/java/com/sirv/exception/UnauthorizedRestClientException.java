package com.sirv.exception;

public class UnauthorizedRestClientException extends RestClientException {
    public UnauthorizedRestClientException(String message, int code) {
        super(message, code);
    }

    public UnauthorizedRestClientException(String message, Throwable cause, int code) {
        super(message, cause, code);
    }

    public UnauthorizedRestClientException(Throwable cause) {
        super(cause);
    }
}
