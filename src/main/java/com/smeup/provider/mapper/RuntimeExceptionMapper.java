package com.smeup.provider.mapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.smeup.provider.model.Error;

@Provider
public class RuntimeExceptionMapper
implements ExceptionMapper<RuntimeException> {

    @Override
    public Response toResponse(final RuntimeException e) {

        final Error error = new Error();
        error.setError(e.getMessage());

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON).entity(error).build();
    }
}
