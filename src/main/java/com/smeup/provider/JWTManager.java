package com.smeup.provider;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

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
        final long iat = System.currentTimeMillis() / 1000L; // issued at claim
        final long exp = iat + 14400L; // expires claim. In this case the token

        final Builder builder = JWT.create().withIssuedAt(new Date(iat))
                .withExpiresAt(new Date(exp));
        privateClaims.forEach((k, v) -> builder.withClaim(k, v));
        return builder.sign(algorithm);
    }

    public Map<String, Object> verify(final String token) {

        try {
            final Algorithm algorithm = Algorithm.HMAC256("secret");
            final JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("auth0").build(); // Reusable verifier instance
            final DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaims().entrySet().stream().collect(Collectors
                    .toMap(e -> e.getKey(), e -> e.getValue().asString()));

        } catch (final UnsupportedEncodingException
                | JWTVerificationException exception) {

            throw new RuntimeException(exception);
        }

    }
}
