package com.sirv.exception;

public class SirvException extends RuntimeException {
    private int code;

    public SirvException(String message, int code) {
        super(message);
        this.code = code;
    }

    public SirvException(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code;
    }

    public SirvException(Throwable cause) {
        super(cause);
    }
}
