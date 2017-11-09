package com.smeup.provider.smeup.connector.as400.operations;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.ibm.as400.access.AS400;
import com.smeup.provider.smeup.connector.as400.as400.qualifiers.Password;
import com.smeup.provider.smeup.connector.as400.as400.qualifiers.Server;
import com.smeup.provider.smeup.connector.as400.as400.qualifiers.User;

public class AS400Producer {

    @Inject @User
    private String user;

    @Inject @Password
    private String password;

    @Inject @Server
    private String server;

    @Produces
    public AS400 provide() {
        return createAS400();
    }

    private AS400 createAS400() {

        final AS400 as400 = new AS400(this.server, this.user, this.password);
        return as400;
    }
}
