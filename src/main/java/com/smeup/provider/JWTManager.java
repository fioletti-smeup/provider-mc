package com.smeup.provider;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;

public class JWTManager {

    //TODO receive secret from outside
    private final static String secret = "SUPER SECRET!";

    final long iat = System.currentTimeMillis() / 1000L; // issued at claim
    final long exp = this.iat + 14400L; // expires claim. In this case the token
    // expires in 14400 seconds

    public String sign(final Map<String, Object> claims) {

        claims.put("exp", this.exp);
        claims.put("iat", this.iat);

        final String jwt = new JWTSigner(secret).sign(claims);
        return jwt;
    }

    public Map<String, Object> verify(final String token) {

        try {
            final JWTVerifier verifier = new JWTVerifier(secret);
            final Map<String, Object> claims = verifier.verify(token);
            return claims;
        } catch (JWTVerifyException | InvalidKeyException | NoSuchAlgorithmException | IllegalStateException
                | SignatureException | IOException e) {
            throw new WebApplicationException(Status.UNAUTHORIZED);
        }
    }
}
