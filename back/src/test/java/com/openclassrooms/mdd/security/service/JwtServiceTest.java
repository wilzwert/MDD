package com.openclassrooms.mdd.security.service;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.security.Key;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/11/2024
 * Time:16:13
 */

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    private final static String SECRET_KEY = "testSecretWithEnoughBytesToGenerateKeyWithoutThrowingWeakKeyException";

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000);
    }

    // TODO
    @Test
    public void shouldThrowWeakKeyException() {
        /*
        byte[] keyBytes = Decoders.BASE64.decode("testSecret");
        assertThrows(WeakKeyException.class, () -> {
            Key key = Keys.hmacShaKeyFor(keyBytes);
            String invalidSignatureToken = Jwts.builder()
                    .subject("testUser")
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 1000))
                    .signWith(key)
                    .compact();
            }
        );*/
    }

    @Test
    public void shouldThrowSignatureException() {
        byte[] keyBytes = Decoders.BASE64.decode("differentTestSecretWithEnoughBytesToGenerateKeyWithoutThrowingWeakKeyException");
        Key key = Keys.hmacShaKeyFor(keyBytes);
        String invalidSignatureToken = Jwts.builder()
                .subject("testUser")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000))
                .signWith(key)
                .compact();

        when(request.getHeader("Authorization")).thenReturn("Bearer "+invalidSignatureToken);

        assertThrows(SignatureException.class, () -> jwtService.extractTokenFromRequest(request));
    }

    @Test
    public void shouldThrowMalformedJwtException() {
        String malformedToken = "yJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJicGF3YW4";

        when(request.getHeader("Authorization")).thenReturn("Bearer "+malformedToken);

        assertThrows(MalformedJwtException.class, () -> jwtService.extractTokenFromRequest(request));
    }

    // TODO
    @Test
    public void shouldThrowExpiredJwtException() {

    }

    // TODO
    @Test
    public void shouldThrowIllegalArgumentException() {
    }

    // TODO
    @Test
    public void shouldThrowUnsupportedJwtException() {

    }
}
