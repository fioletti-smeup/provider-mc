package com.smeup.provider.mapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.smeup.provider.model.AuthorizationException;
import com.smeup.provider.model.Error;

@Provider
public class AuthorizationExceptionMapper
implements ExceptionMapper<AuthorizationException> {

    @Override
    public Response toResponse(final AuthorizationException e) {

        final Error error = new Error();
        error.setError(e.getMessage());

        return Response.status(Response.Status.UNAUTHORIZED)
                .type(MediaType.APPLICATION_JSON).entity(error).build();
    }
}
