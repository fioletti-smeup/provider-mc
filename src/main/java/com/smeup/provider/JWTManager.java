package com.smeup.provider;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JWTManager {

    // TODO receive secret from outside
    private final static String SECRET = "SUPER SECRET!";

    public String sign(final Map<String, String> privateClaims) {

        Algorithm algorithm = null;

        try {
            algorithm = Algorithm.HMAC256(SECRET);
        } catch (IllegalArgumentException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        final Instant now = Instant.now();
        final Date iat = Date.from(now); // issued at claim
        final Date exp = Date.from(now.plus(Duration.ofHours(4))); // expires claim. In this case the token

        final Builder builder = JWT.create().withIssuedAt(iat).withExpiresAt(exp);
        privateClaims.forEach((k, v) -> builder.withClaim(k, v));
        return builder.sign(algorithm);
    }

    public DecodedJWT verify(final String token) {

        try {
            final Algorithm algorithm = Algorithm.HMAC256(SECRET);
            final JWTVerifier verifier = JWT.require(algorithm).build(); // Reusable verifier instance
            return verifier.verify(token);

        } catch (final UnsupportedEncodingException
                | JWTVerificationException exception) {

            throw new RuntimeException(exception);
        }

    }
}
