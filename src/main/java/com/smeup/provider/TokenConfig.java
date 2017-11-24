package com.smeup.provider;

import java.io.Serializable;
import java.time.Duration;

import javax.enterprise.inject.Any;

@Any
public class TokenConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private String secret;

    private Duration duration = Duration.ofMinutes(30);

    public String getSecret() {
        return this.secret;
    }

    public void setSecret(final String secret) {
        this.secret = secret;
    }

    public Duration getDuration() {
        return this.duration;
    }

    public void setDuration(final Duration duration) {
        this.duration = duration;
    }
}
