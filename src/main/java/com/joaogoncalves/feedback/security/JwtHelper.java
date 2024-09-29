package com.joaogoncalves.feedback.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.joaogoncalves.feedback.entity.RefreshToken;
import com.joaogoncalves.feedback.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class JwtHelper {
    static final String issuer = "customer-feedback-service";

    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    private final Algorithm accessTokenAlgorithm;
    private final Algorithm refreshTokenAlgorithm;
    private final JWTVerifier accessTokenVerifier;
    private final JWTVerifier refreshTokenVerifier;

    public JwtHelper(@Value("${accessTokenSecret}") final String accessTokenSecret,
                     @Value("${refreshTokenSecret}") final String refreshTokenSecret,
                     @Value("${refreshTokenExpirationDays}") final int refreshTokenExpirationDays,
                     @Value("${accessTokenExpirationMinutes}") final int accessTokenExpirationMinutes) {
        accessTokenExpirationMs = (long) accessTokenExpirationMinutes * 60 * 1000;
        refreshTokenExpirationMs = (long) refreshTokenExpirationDays * 24 * 60 * 60 * 1000;
        accessTokenAlgorithm = Algorithm.HMAC512(accessTokenSecret);
        refreshTokenAlgorithm = Algorithm.HMAC512(refreshTokenSecret);
        accessTokenVerifier = JWT.require(accessTokenAlgorithm)
                .withIssuer(issuer)
                .build();
        refreshTokenVerifier = JWT.require(refreshTokenAlgorithm)
                .withIssuer(issuer)
                .build();
    }

    public String generateAccessToken(final User user) {
        final String token =  JWT.create()
                .withIssuer(issuer)
                .withSubject(user.getId())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(new Date().getTime() + accessTokenExpirationMs))
                .sign(accessTokenAlgorithm);
        log.debug("Generated access token for user [ID: {}]", user.getId());
        return token;
    }

    public String generateRefreshToken(final User user, final RefreshToken refreshToken) {
        final String token = JWT.create()
                .withIssuer(issuer)
                .withSubject(user.getId())
                .withClaim("tokenId", refreshToken.getId())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date((new Date()).getTime() + refreshTokenExpirationMs))
                .sign(refreshTokenAlgorithm);
        log.debug("Generated refresh token for user [ID: {}]", user.getId());
        return token;
    }

    private DecodedJWT decodeAccessToken(final String token) {
        return accessTokenVerifier.verify(token);
    }

    private DecodedJWT decodeRefreshToken(final String token) {
        return refreshTokenVerifier.verify(token);
    }

    public String getUserIdFromAccessToken(final String token) {
        return decodeAccessToken(token).getSubject();
    }

    public String getUserIdFromRefreshToken(final String token) {
        return decodeRefreshToken(token).getSubject();
    }

    public String getTokenIdFromRefreshToken(final String token) {
        return decodeRefreshToken(token).getClaim("tokenId").asString();
    }
}
