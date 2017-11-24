package com.smeup.provider;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.smeup.provider.model.SmeupSession;

@ApplicationScoped
public class TokenManager implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private TokenConfig tokenConfig;

    private Algorithm algorithm;

    private JWTVerifier jwtVerifier;

    @Inject
    private SmeupSession smeupSession;

    @PostConstruct
    public void init() {

        try {
            setAlgorithm(Algorithm.HMAC256(getSecret()));
            setJWTVerifier(JWT.require(getAlgorithm()).build());
        } catch (IllegalArgumentException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String sign() {

        final Map<String, String> claims = new HashMap<>();
        claims.put(Claims.SESSION_ID.name(),
                String.valueOf(getSmeupSession().getSessionId()));
        claims.put(Claims.CCSID.name(),
                String.valueOf(getSmeupSession().getCCSID()));
        final String jwt = sign(claims);
        return jwt;
    }

    private String sign(final Map<String, String> privateClaims) {

        final Instant now = Instant.now();
        final Date iat = Date.from(now); // issued at claim
        final Date exp = Date.from(now.plus(getDuration())); // expires claim. In this case the token

        final Builder builder = JWT.create().withIssuedAt(iat)
                .withExpiresAt(exp);
        privateClaims.forEach((k, v) -> builder.withClaim(k, v));
        return builder.sign(getAlgorithm());
    }

    public DecodedJWT verify(final String token) {

        try {
            return getJWTVerifier().verify(token);

        } catch (final JWTVerificationException exception) {

            throw new TokenVerificationException(exception);
        }
    }

    public String getSecret() {
        return getTokenConfig().getSecret();
    }

    public Duration getDuration() {
        return getTokenConfig().getDuration();
    }

    public Algorithm getAlgorithm() {
        return this.algorithm;
    }

    public void setAlgorithm(final Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public JWTVerifier getJWTVerifier() {
        return this.jwtVerifier;
    }

    public void setJWTVerifier(final JWTVerifier jwtVerifier) {
        this.jwtVerifier = jwtVerifier;
    }

    public TokenConfig getTokenConfig() {
        return this.tokenConfig;
    }

    public void setTokenConfig(final TokenConfig tokenConfig) {
        this.tokenConfig = tokenConfig;
    }

    public SmeupSession getSmeupSession() {
        return this.smeupSession;
    }

    public void setSmeupSession(final SmeupSession smeupSession) {
        this.smeupSession = smeupSession;
    }
}
