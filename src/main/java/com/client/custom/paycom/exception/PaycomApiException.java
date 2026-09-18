package com.client.custom.paycom.exception;

import org.springframework.http.HttpStatus;

/**
 * Wraps any failure talking to Paycom - a rejected call, a connectivity failure, or a response we
 * could not parse - so callers get a single exception type carrying the status/body Paycom
 * actually returned, rather than raw RestTemplate exceptions.
 */
public class PaycomApiException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String responseBody;

    public PaycomApiException(String message) {
        this(message, null, null, null);
    }

    public PaycomApiException(String message, Throwable cause) {
        this(message, null, null, cause);
    }

    public PaycomApiException(String message, HttpStatus httpStatus, String responseBody) {
        this(message, httpStatus, responseBody, null);
    }

    public PaycomApiException(String message, HttpStatus httpStatus, String responseBody, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.responseBody = responseBody;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
