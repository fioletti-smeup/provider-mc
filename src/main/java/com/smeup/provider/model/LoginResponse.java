package com.smeup.provider.model;

import java.io.Serializable;

public class LoginResponse implements Serializable{

    private static final long serialVersionUID = 1L;

    public static class Data implements Serializable{

        private static final long serialVersionUID = 1L;

        private String jwt;

        private String initXML;

        public String getJWT() {
            return this.jwt;
        }

        public void setJWT(final String jwt) {
            this.jwt = jwt;
        }

        public String getInitXML() {
            return this.initXML;
        }

        public void setInitXML(final String initXML) {
            this.initXML = initXML;
        }
    }

    private Data data;

    public Data getData() {
        return this.data;
    }

    public void setData(final Data data) {
        this.data = data;
    }
}
