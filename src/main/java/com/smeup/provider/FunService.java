package com.smeup.provider;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smeup.provider.log.Logged;
import com.smeup.provider.smeup.connector.as400.operations.FunHandler;
import com.smeup.provider.token.Secured;

@Path("fun")
@Secured
@Produces(MediaType.APPLICATION_XML)
@Logged
@ApplicationScoped
public class FunService {

    private static final String FUN_PARAM = "fun";

    private static final Logger LOGGER = LoggerFactory
            .getLogger(FunService.class);

    @Inject
    private FunHandler funHandler;

    @POST
    public Response invoke(@FormParam(FUN_PARAM) final String fun) {

        LOGGER.info("Fun called: " + fun);
        final StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(final OutputStream os)
                    throws IOException, WebApplicationException {
                final Writer writer = new BufferedWriter(
                        new OutputStreamWriter(os));
                getFunHandler().executeFun(fun, writer);
                writer.flush();
            }
        };

        return Response.ok(stream).build();
    }

    public FunHandler getFunHandler() {
        return this.funHandler;
    }

    public void setFunHandler(final FunHandler funHandler) {
        this.funHandler = funHandler;
    }
}
