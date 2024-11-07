package com.openclassrooms.mdd.service;


import com.openclassrooms.mdd.model.JwtToken;
import com.openclassrooms.mdd.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
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

    public Optional<JwtToken> extractTokenFromRequest(HttpServletRequest request) throws ExpiredJwtException, MalformedJwtException, SecurityException, IllegalArgumentException {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("Authorization header not found or not compatible with Bearer token");
            return Optional.empty();
        }
        final String token = authHeader.substring(7);

        Jwt<?, ?> parsedToken = Jwts
                .parser().verifyWith(getSignInKey()).build().parse(token);
        Claims claims = (Claims) parsedToken.getPayload();

        JwtToken jwtToken = new JwtToken(claims.getSubject(), claims);
        return Optional.of(jwtToken);
    }

    public String generateToken(User user) {
        log.info("Generating JWT token for user {}", user.getEmail());
        return Jwts
                .builder()
                .subject(user.getEmail())
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

