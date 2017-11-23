package com.smeup.provider;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.smeup.provider.smeup.connector.as400.operations.FunHandler;

import io.swagger.oas.annotations.Operation;

@Path("fun")
@Secured
@Produces(MediaType.APPLICATION_XML)
public class FunService {

    private static final String FUN_PARAM = "fun";

    @Inject
    private FunHandler funHandler;

    @POST
    @Operation
    public Response invoke(@FormParam(FUN_PARAM) final String fun) {

        return Response.ok(getFunHandler().executeFun(fun)).build();
    }

    public FunHandler getFunHandler() {
        return this.funHandler;
    }

    public void setFunHandler(final FunHandler funHandler) {
        this.funHandler = funHandler;
    }
}
