package com.smeup.provider.model;

import com.smeup.provider.smeup.connector.as400.as400.qualifiers.DoNotProduceAutomatically;

@DoNotProduceAutomatically
public class Credentials {

    private String user;
    private String password;
    private String environment;

    public String getUser() {
        return this.user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getEnvironment() {
        return this.environment;
    }

    public void setEnvironment(final String environment) {
        this.environment = environment;
    }
}
