package com.smeup.provider.model;

public class AuthorizationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AuthorizationException(final Throwable cause) {
        super(cause);
    }

    public AuthorizationException(final String message) {
        super(message);
    }

}
