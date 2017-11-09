package com.smeup.provider.model;

import java.io.Serializable;

public class SmeupSession implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Claims {
        SERVER, CCSID, SESSION_ID
    }

    private String server;
    private String user;
    private String password;
    private Integer CCSID;
    private String environment;
    private String sessionId;

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

    public Integer getCCSID() {
        return this.CCSID;
    }

    public void setCCSID(final Integer cCSID) {
        this.CCSID = cCSID;
    }

    public String getEnvironment() {
        return this.environment;
    }

    public void setEnvironment(final String environment) {
        this.environment = environment;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }
}
