package com.smeup.provider;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.smeup.provider.model.Credentials;
import com.smeup.provider.model.SmeupSession;
import com.smeup.provider.smeup.connector.as400.operations.LoginHandler;

@Path("AuthenticateService")
@Produces(MediaType.APPLICATION_XML)
@RequestScoped
public class LoginService {

    @Inject
    private LoginHandler loginHandler;

    private Credentials credentials;

    @Inject
    private SmeupSession smeupSession;

    @Inject
    private JWTManager jwtManager;

    @POST
    public Response login(@FormParam("usr") final String user,
            @FormParam("pwd") final String password,
            @FormParam("env") final String environment,
            @FormParam("ccsid") @DefaultValue("1144") final int ccsid) {

        final Credentials credentials = new Credentials();
        credentials.setUser(user);
        credentials.setPassword(password);
        credentials.setEnvironment(environment);
        setCredentials(credentials);

        getSmeupSession().setCCSID(ccsid);

        final String initXML = getLoginHandler().login();

        if (null != initXML && !initXML.trim().isEmpty()) {

            final Map<String, String> claims = new HashMap<>();
            claims.put(Claims.SESSION_ID.name(),
                    String.valueOf(this.smeupSession.getSessionId()));
            claims.put(Claims.CCSID.name(),
                    String.valueOf(this.smeupSession.getCCSID()));
            final String jwt = getJWTManager().sign(claims);
            return Response.ok(initXML).header("Authorization", "Bearer " + jwt)
                    .build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
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

    public JWTManager getJWTManager() {
        return this.jwtManager;
    }

    public void setJWTManager(final JWTManager jwtManager) {
        this.jwtManager = jwtManager;
    }
}
