package com.cmgmtfs.calcify.provider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.cmgmtfs.calcify.domain.UserPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

@Component
public class TokenProvider {
    public static final String AUTHORITIES = "authorities";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    private static final String C_MGMT_FS = "C_MGMT_FS";
    private static final String CUSTOMER_MANAGEMENT_SERVICE = "CUSTOMER_MANAGEMENT_SERVICE";
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 1_800_000; // 30 minutes
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 432_000_000; // 5 days

    @Value("${jwt.secret}")
    private String secret;

    public String createAccessToken(UserPrincipal userPrincipal) {
        String[] claims = getClaimsFromUser(userPrincipal);
        return JWT.create()
                .withIssuer(C_MGMT_FS)
                .withAudience(CUSTOMER_MANAGEMENT_SERVICE)
                .withIssuedAt(new Date())
                .withSubject(
                        userPrincipal.getUsername())
                .withArrayClaim(AUTHORITIES, claims)
                .withExpiresAt(new Date(currentTimeMillis() * ACCESS_TOKEN_EXPIRATION_TIME))
                .sign(
                        HMAC512(secret.getBytes()));
    }

    public String createRefreshToken(UserPrincipal userPrincipal) {
        String[] claims = getClaimsFromUser(userPrincipal);
        return JWT.create()
                .withIssuer(C_MGMT_FS)
                .withAudience(CUSTOMER_MANAGEMENT_SERVICE)
                .withIssuedAt(new Date())
                .withSubject(
                        userPrincipal.getUsername())
                .withExpiresAt(new Date(currentTimeMillis() * REFRESH_TOKEN_EXPIRATION_TIME))
                .sign(
                        HMAC512(secret.getBytes()));
    }

    public List<GrantedAuthority> getAuthorities(String token) {
        String[] claims = getClaimsFromToken(token);
        return stream(claims).map(SimpleGrantedAuthority::new).collect(toList());
    }

    private String[] getClaimsFromToken(String token) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
    }

    private String[] getClaimsFromUser(UserPrincipal userPrincipal) {
        return userPrincipal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toArray(String[]::new);
    }

    private JWTVerifier getJWTVerifier() {
        JWTVerifier verifier;
        try {
           Algorithm algorithm = HMAC512(secret);
           verifier = JWT.require(algorithm).withIssuer(C_MGMT_FS).build();
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED, e);
        }
        return verifier;
    }
}
