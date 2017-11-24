package com.smeup.provider.model;

import javax.enterprise.inject.Any;

@Any
public class FixedCredentials {

    private String server;
    private String user;
    private String password;

    public String getServer() {
        return this.server;
    }

    public void setServer(final String server) {
        this.server = server;
    }

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
}
