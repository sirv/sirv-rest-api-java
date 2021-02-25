package com.sirv.exception;

public class RestClientException extends SirvException {
    public RestClientException(String message, int code) {
        super(message, code);
    }

    public RestClientException(String message, Throwable cause, int code) {
        super(message, cause, code);
    }

    public RestClientException(Throwable cause) {
        super(cause);
    }
}
