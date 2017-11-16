package com.smeup.provider;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.smeup.provider.smeup.connector.as400.LogoutHandler;

@Secured
@Path("logout")
@Produces(MediaType.APPLICATION_JSON)
public class LogoutProvider {

    @Inject
    private LogoutHandler logoutHandler;

    @POST
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