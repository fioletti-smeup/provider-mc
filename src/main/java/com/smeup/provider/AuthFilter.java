package com.smeup.provider;

import java.io.IOException;
import java.util.Map;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.google.common.net.HttpHeaders;
import com.smeup.provider.model.SmeupSession;
import com.smeup.provider.model.SmeupSession.Claims;

@Secured
@Provider
public class AuthFilter implements ContainerRequestFilter {

    @Inject
    private Event<SmeupSession> smeupSessionEmitter;

    @Override
    public void filter(final ContainerRequestContext requestContext)
            throws IOException {

        // Get the HTTP Authorization header from the request
        final String authorizationHeader = requestContext
                .getHeaderString(HttpHeaders.AUTHORIZATION);

        // Check if the HTTP Authorization header is present and formatted
        // correctly
        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException(
                    "Authorization header must be provided");
        }

        // Extract the token from the HTTP Authorization header
        final String token = authorizationHeader.substring("Bearer".length())
                .trim();

        try {

            // Validate the token
            final SmeupSession smeupSession = validateToken(token);
            this.smeupSessionEmitter.fire(smeupSession);

        } catch (final Exception e) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }

    private SmeupSession validateToken(final String token) throws Exception {

        final JWTManager jwtManager = new JWTManager();
        final Map<String, Object> claims = jwtManager.verify(token);
        final SmeupSession smeupSession = new SmeupSession();
        smeupSession.setServer(
                claims.get(Claims.SERVER.name()).toString());
        smeupSession.setSessionId(claims.get(Claims.SESSION_ID.name()).toString());
        smeupSession.setCCSID(Integer.valueOf(
                claims.get(Claims.CCSID.name()).toString()));
        return smeupSession;
    }
}
