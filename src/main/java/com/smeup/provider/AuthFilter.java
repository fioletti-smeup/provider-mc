package com.smeup.provider;

import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.auth0.jwt.interfaces.Claim;
import com.google.common.net.HttpHeaders;
import com.smeup.provider.model.SmeupSession;

@Secured
@Provider
public class AuthFilter implements ContainerRequestFilter {

    @Inject
    private SmeupSession smeupSession;

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
        final String token = authorizationHeader.substring("Bearer ".length());

        try {

            // Validate the token
            validateToken(token);

        } catch (final Exception e) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }

    private void validateToken(final String token) throws Exception {

        final JWTManager jwtManager = new JWTManager();
        final Map<String, Claim> claims = jwtManager.verify(token).getClaims();
        getSmeupSession()
        .setSessionId(claims.get(Claims.SESSION_ID.name()).asString());
        getSmeupSession().setCCSID(
                Integer.valueOf(claims.get(Claims.CCSID.name()).asString()));
    }

    public SmeupSession getSmeupSession() {
        return this.smeupSession;
    }

    public void setSmeupSession(final SmeupSession smeupSession) {
        this.smeupSession = smeupSession;
    }
}
