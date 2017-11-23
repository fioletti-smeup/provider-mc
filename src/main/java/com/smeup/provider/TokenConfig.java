package com.smeup.provider;

import java.io.Serializable;
import java.time.Duration;

import com.smeup.provider.smeup.connector.as400.as400.qualifiers.DoNotProduceAutomatically;

@DoNotProduceAutomatically
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
