package com.smeup.provider.filter;

import java.io.IOException;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import com.auth0.jwt.interfaces.Claim;
import com.google.common.net.HttpHeaders;
import com.smeup.provider.log.Logged;
import com.smeup.provider.model.AuthorizationException;
import com.smeup.provider.model.SmeupSession;
import com.smeup.provider.token.Claims;
import com.smeup.provider.token.Secured;
import com.smeup.provider.token.TokenManager;

@Secured
@Provider
@ApplicationScoped
@Logged
public class AuthFilter implements ContainerRequestFilter {

    private static final String NO_TOKEN_FOUND = "Authorization Token not found";

    private SmeupSession smeupSession = new SmeupSession();

    @Inject
    private TokenManager tokenManager;

    private static final String BEARER_ = "Bearer ";

    @Override
    public void filter(final ContainerRequestContext requestContext)
            throws IOException {

        final String authorizationHeader = requestContext
                .getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null
                || !authorizationHeader.startsWith(BEARER_)) {

            throw new AuthorizationException(NO_TOKEN_FOUND);
        }

        final String token = authorizationHeader.substring(BEARER_.length());

        validateToken(token);

    }

    private void validateToken(final String token) {

        final Map<String, Claim> claims = getTokenManager().verify(token)
                .getClaims();
        getSmeupSession().setSessionId(claims.get(Claims.SESSION_ID.name()).asString());
        getSmeupSession().setCCSID(
                Integer.valueOf(claims.get(Claims.CCSID.name()).asString()));
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
