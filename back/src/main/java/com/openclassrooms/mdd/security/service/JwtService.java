package com.openclassrooms.mdd.security.service;


import com.openclassrooms.mdd.model.JwtToken;
import com.openclassrooms.mdd.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;


/**
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:16:06
 */

@Service
@Slf4j
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    public Optional<JwtToken> extractTokenFromRequest(HttpServletRequest request) throws ExpiredJwtException, MalformedJwtException, IllegalArgumentException, UnsupportedJwtException, SignatureException {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("Authorization header not found or not compatible with Bearer token");
            return Optional.empty();
        }
        final String token = authHeader.substring(7);

        try {
            Jwt<?, ?> parsedToken = Jwts
                    .parser().verifyWith(getSignInKey()).build().parse(token);
            Claims claims = (Claims) parsedToken.getPayload();

            JwtToken jwtToken = new JwtToken(claims.getSubject(), claims);
            return Optional.of(jwtToken);
        }
        // we only catch different JwtException types to log warning messages
        // the exceptions are then thrown again to be handled by the authentication filter
        catch (ExpiredJwtException e) {
            log.warn("Expired JWT token: {}", e.getMessage());
            throw e;
        }
        catch (MalformedJwtException e) {
            log.warn("Malformed JWT token: {}", e.getMessage());
            throw e;
        }
        catch (SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
            throw e;
        }
        catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
            throw e;
        }
        catch (IllegalArgumentException e) {
            log.warn("Empty JWT claims: {}", e.getMessage());
            throw e;
        }
    }

    public String generateToken(User user) {
        log.info("Generating JWT token for user {} {}", user.getEmail(), user.getId());
        return Jwts
                .builder()
                .subject(String.valueOf(user.getId()))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey())
                .compact();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

