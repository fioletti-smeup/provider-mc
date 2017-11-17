package com.smeup.provider.model;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class SmeupSession implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer CCSID;
    private String sessionId;

    public Integer getCCSID() {
        return this.CCSID;
    }

    public void setCCSID(final Integer cCSID) {
        this.CCSID = cCSID;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }
}
