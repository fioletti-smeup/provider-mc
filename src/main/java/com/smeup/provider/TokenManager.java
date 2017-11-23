package com.smeup.provider;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
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
import com.smeup.provider.conf.Secret;

@ApplicationScoped
public class TokenManager implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    @Secret
    private String secret;

    private Algorithm algorithm;

    private JWTVerifier jwtVerifier;

    @PostConstruct
    public void init() {

        try {
            setAlgorithm(Algorithm.HMAC256(getSecret()));
            setJWTVerifier(JWT.require(getAlgorithm()).build());
        } catch (IllegalArgumentException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String sign(final Map<String, String> privateClaims) {

        final Instant now = Instant.now();
        final Date iat = Date.from(now); // issued at claim
        final Date exp = Date.from(now.plus(Duration.ofHours(4))); // expires claim. In this case the token

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
        return this.secret;
    }

    public void setSecret(final String secret) {
        this.secret = secret;
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
}
