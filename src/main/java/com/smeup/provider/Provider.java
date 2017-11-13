package com.smeup.provider;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.smeup.provider.model.FunResponse;
import com.smeup.provider.model.LoginResponse;
import com.smeup.provider.model.SmeupSession;
import com.smeup.provider.smeup.connector.as400.LogoutHandler;
import com.smeup.provider.smeup.connector.as400.operations.FunHandler;
import com.smeup.provider.smeup.connector.as400.operations.LoginHandler;

@Path("")
public class Provider {

    @Inject
    private FunHandler funHandler;

    @Inject
    private LoginHandler loginHandler;

    @Inject
    private LogoutHandler logoutHandler;

    @Inject
    private SmeupSession smeupSession;

    @Secured
    @Path("fun")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    // TODO retrieve sessionId, ccsid and server from jwt
    public Response invoke(@FormParam("fun") final String fun) {

        final FunResponse.Data data = getFunHandler().executeFun(fun);
        final FunResponse response = new FunResponse();
        response.setData(data);
        return Response.ok(data).build();
    }

    @Secured
    @Path("logout")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    // TODO retrieve sessionId, ccsid and server from jwt
    public Response logout() {

        getLogoutHandler().logout();
        return Response.ok("").build();
    }

    @Path("login")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@FormParam("user") final String user,
            @FormParam("password") final String password,
            @FormParam("env") final String environment,
            @FormParam("ccsid") final int ccsid,
            @FormParam("server") final String server) {

        getSmeupSession().setServer(server);
        getSmeupSession().setUser(user);
        getSmeupSession().setEnvironment(environment);
        getSmeupSession().setPassword(password);
        getSmeupSession().setCCSID(ccsid);
        final LoginResponse.Data data = getLoginHandler().login();
        final LoginResponse login = new LoginResponse();
        login.setData(data);
        return Response.ok(login).build();
    }

    public FunHandler getFunHandler() {
        return this.funHandler;
    }

    public void setFunHandler(final FunHandler funHandler) {
        this.funHandler = funHandler;
    }

    public LoginHandler getLoginHandler() {
        return this.loginHandler;
    }

    public void setLoginHandler(final LoginHandler loginHandler) {
        this.loginHandler = loginHandler;
    }

    public LogoutHandler getLogoutHandler() {
        return this.logoutHandler;
    }

    public void setLogoutHandler(final LogoutHandler logoutHandler) {
        this.logoutHandler = logoutHandler;
    }

    public SmeupSession getSmeupSession() {
        return this.smeupSession;
    }

    public void setSmeupSession(final SmeupSession smeupSession) {
        this.smeupSession = smeupSession;
    }
}
