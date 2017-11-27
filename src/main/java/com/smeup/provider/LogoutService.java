package com.smeup.provider;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.smeup.provider.log.Logged;
import com.smeup.provider.smeup.connector.as400.LogoutHandler;
import com.smeup.provider.token.Secured;

@Secured
@Path("DisconnectService")
@Produces(MediaType.APPLICATION_JSON)
@Logged
public class LogoutService {

    @Inject
    private LogoutHandler logoutHandler;

    @DELETE
    public Response logout() {

        getLogoutHandler().logout();
        return Response.ok("").build();
    }

    public LogoutHandler getLogoutHandler() {
        return this.logoutHandler;
    }

    public void setLogoutHandler(final LogoutHandler logoutHandler) {
        this.logoutHandler = logoutHandler;
    }
}