package com.smeup.provider.mapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.smeup.provider.TokenVerificationException;
import com.smeup.provider.model.Error;

@Provider
public class TokenVerificationExceptionMapper
implements ExceptionMapper<TokenVerificationException> {

    @Override
    public Response toResponse(final TokenVerificationException e) {

        final Error error = new Error();
        error.setError(e.getMessage());

        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON).entity(error).build();
    }
}
