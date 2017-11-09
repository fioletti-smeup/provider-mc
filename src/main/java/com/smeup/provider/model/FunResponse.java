package com.smeup.provider.model;

import java.io.Serializable;

public class FunResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    public static class Data implements Serializable {

        private static final long serialVersionUID = 1L;
        private String response;

        public String getResponse() {
            return this.response;
        }

        public void setResponse(final String response) {
            this.response = response;
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
