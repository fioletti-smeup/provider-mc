package com.smeup.provider.token;

public class TokenVerificationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TokenVerificationException(final Throwable cause) {
        super(cause);
    }
}
