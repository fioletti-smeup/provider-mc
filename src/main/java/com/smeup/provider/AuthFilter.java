package com.smeup.provider;

import java.io.IOException;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
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
@ApplicationScoped
public class AuthFilter implements ContainerRequestFilter {

    private SmeupSession smeupSession = new SmeupSession();

    @Inject
    private TokenManager tokenManager;

    private static final String BEARER_ = "Bearer ";

    @Override
    public void filter(final ContainerRequestContext requestContext)
            throws IOException {

        // Get the HTTP Authorization header from the request
        final String authorizationHeader = requestContext
                .getHeaderString(HttpHeaders.AUTHORIZATION);

        // Check if the HTTP Authorization header is present and formatted
        // correctly
        if (authorizationHeader == null
                || !authorizationHeader.startsWith(BEARER_)) {
            throw new NotAuthorizedException(
                    "Authorization header must be provided");
        }

        // Extract the token from the HTTP Authorization header
        final String token = authorizationHeader.substring(BEARER_.length());

        try {

            // Validate the token and set SmeupSession
            validateToken(token);

        } catch (final TokenVerificationException e) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }

    private void validateToken(final String token) {

        final Map<String, Claim> claims = getTokenManager().verify(token).getClaims();
        final SmeupSession session = new SmeupSession();
        session.setSessionId(claims.get(Claims.SESSION_ID.name()).asString());
        getSmeupSession().setCCSID(
                Integer.valueOf(claims.get(Claims.CCSID.name()).asString()));
        setSmeupSession(session);
    }

    @Produces
    @RequestScoped
    public SmeupSession getSmeupSession() {
        return this.smeupSession;
    }

    public void setSmeupSession(final SmeupSession smeupSession) {
        this.smeupSession = smeupSession;
    }

    public TokenManager getTokenManager() {
        return this.tokenManager;
    }

    public void setTokenManager(final TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }
}
