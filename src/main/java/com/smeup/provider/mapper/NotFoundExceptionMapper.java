package com.smeup.provider.mapper;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.smeup.provider.model.Error;

@Provider
public class NotFoundExceptionMapper
implements ExceptionMapper<NotFoundException> {

    @Override
    public Response toResponse(final NotFoundException e) {

        final Error error = new Error();
        error.setError(e.getMessage());

        return Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.APPLICATION_JSON).entity(error).build();
    }
}
