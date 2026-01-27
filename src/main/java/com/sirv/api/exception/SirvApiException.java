package com.sirv.api.exception;

/**
 * Exception thrown when the Sirv API returns an error.
 */
public class SirvApiException extends RuntimeException {
    private final int statusCode;
    private final String errorCode;

    /**
     * Creates a new SirvApiException.
     *
     * @param message    The error message
     * @param statusCode The HTTP status code
     */
    public SirvApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = null;
    }

    /**
     * Creates a new SirvApiException.
     *
     * @param message    The error message
     * @param statusCode The HTTP status code
     * @param errorCode  The API error code
     */
    public SirvApiException(String message, int statusCode, String errorCode) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    /**
     * Creates a new SirvApiException with a cause.
     *
     * @param message    The error message
     * @param statusCode The HTTP status code
     * @param cause      The underlying cause
     */
    public SirvApiException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.errorCode = null;
    }

    /**
     * Gets the HTTP status code.
     *
     * @return The HTTP status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Gets the API error code.
     *
     * @return The API error code, or null if not available
     */
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        if (errorCode != null) {
            return String.format("SirvApiException(%d): %s - %s", statusCode, errorCode, getMessage());
        }
        return String.format("SirvApiException(%d): %s", statusCode, getMessage());
    }
}
