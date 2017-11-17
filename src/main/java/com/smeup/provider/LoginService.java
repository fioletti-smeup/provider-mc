package com.smeup.provider;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.smeup.provider.model.Credentials;
import com.smeup.provider.model.LoginResponse;
import com.smeup.provider.model.SmeupSession;
import com.smeup.provider.smeup.connector.as400.operations.LoginHandler;

@Path("login")
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class LoginService {

    @Inject
    private LoginHandler loginHandler;

    private Credentials credentials;

    @Inject
    private SmeupSession smeupSession;

    @POST
    public Response login(@FormParam("user") final String user,
            @FormParam("password") final String password,
            @FormParam("env") final String environment,
            @FormParam("ccsid") final int ccsid) {

        final Credentials credentials = new Credentials();
        credentials.setUser(user);
        credentials.setPassword(password);
        credentials.setEnvironment(environment);
        setCredentials(credentials);

        getSmeupSession().setCCSID(ccsid);
        final LoginResponse.Data data = getLoginHandler().login();
        final LoginResponse login = new LoginResponse();
        login.setData(data);
        return Response.ok(login).build();
    }

    public LoginHandler getLoginHandler() {
        return this.loginHandler;
    }

    public void setLoginHandler(final LoginHandler loginHandler) {
        this.loginHandler = loginHandler;
    }

    @javax.enterprise.inject.Produces
    @RequestScoped
    public Credentials getCredentials() {
        return this.credentials;
    }

    public void setCredentials(final Credentials credentials) {
        this.credentials = credentials;
    }

    public SmeupSession getSmeupSession() {
        return this.smeupSession;
    }

    public void setSmeupSession(final SmeupSession smeupSession) {
        this.smeupSession = smeupSession;
    }
}
