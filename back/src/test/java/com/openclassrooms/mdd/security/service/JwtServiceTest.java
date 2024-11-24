package com.openclassrooms.mdd.security.service;


import com.openclassrooms.mdd.model.JwtToken;
import com.openclassrooms.mdd.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.security.WeakKeyException;
import jakarta.servlet.http.HttpServletRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

    @Test
    public void shouldThrowWeakKeyException() {
        ReflectionTestUtils.setField(jwtService, "secretKey", "weakKey");
        User user = new User().setId(1).setEmail("test@example.com");

        assertThrows(WeakKeyException.class, () -> jwtService.generateToken(user));
    }

    @Test
    public void shouldGenerateToken() {
        User user = new User().setId(1).setEmail("test@example.com");
        String token = jwtService.generateToken(user);

        assertThat(token).isNotBlank();
        long count = token.chars().filter(ch -> ch == '.').count();
        Assertions.assertThat(count).isEqualTo(2);
    }

    @Nested
    class ExtractJwtTokenTest {
        @Test
        public void shouldReturnEmptyOptional() {
            when(request.getHeader("Authorization")).thenReturn(null);

            assertThat(jwtService.extractTokenFromRequest(request)).isEmpty();

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

            when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidSignatureToken);

            assertThrows(SignatureException.class, () -> jwtService.extractTokenFromRequest(request));
        }

        @Test
        public void shouldThrowMalformedJwtException() {
            String malformedToken = "yJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJicGF3YW4";

            when(request.getHeader("Authorization")).thenReturn("Bearer " + malformedToken);

            assertThrows(MalformedJwtException.class, () -> jwtService.extractTokenFromRequest(request));
        }

        @Test
        public void shouldThrowExpiredJwtException() {
            byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
            Key key = Keys.hmacShaKeyFor(keyBytes);
            String expiredToken = Jwts.builder()
                    .subject("testUser")
                    .issuedAt(new Date(System.currentTimeMillis() - 10000000))
                    .expiration(new Date(System.currentTimeMillis() - 1000000))
                    .signWith(key)
                    .compact();

            when(request.getHeader("Authorization")).thenReturn("Bearer " + expiredToken);

            assertThrows(ExpiredJwtException.class, () -> jwtService.extractTokenFromRequest(request));
        }

        @Test
        public void shouldThrowIllegalArgumentException() {
            when(request.getHeader("Authorization")).thenReturn("Bearer ");

            assertThrows(IllegalArgumentException.class, () -> jwtService.extractTokenFromRequest(request));
        }

        @Test
        public void shouldThrowUnsupportedJwtException() {
            String unsupportedToken = Jwts.builder()
                    .subject("testUser")
                    .compact();

            when(request.getHeader("Authorization")).thenReturn("Bearer " + unsupportedToken);

            assertThrows(UnsupportedJwtException.class, () -> jwtService.extractTokenFromRequest(request));
        }

        @Test
        public void shouldExtractJwtToken() {
            byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
            Key key = Keys.hmacShaKeyFor(keyBytes);
            String validToken = Jwts.builder()
                    .subject("test@example.com")
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 1000))
                    .signWith(key)
                    .compact();

            when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);

            Optional<JwtToken> jwtToken = jwtService.extractTokenFromRequest(request);

            assertThat(jwtToken).isPresent();
            assertThat(jwtToken.get().getSubject()).isEqualTo("test@example.com");
            assertThat(jwtToken.get().getClaims().getSubject()).isEqualTo("test@example.com");
        }

        @Test
        public void shouldExtractFromGeneratedToken() {
            User user = new User().setId(1).setEmail("test@example.com");
            String token = jwtService.generateToken(user);

            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

            Optional<JwtToken> jwtToken = jwtService.extractTokenFromRequest(request);

            assertThat(token).isNotBlank();
            assertThat(jwtToken).isPresent();
            assertThat(jwtToken.get().getSubject()).isEqualTo("1");
            assertThat(jwtToken.get().getClaims().getSubject()).isEqualTo("1");
        }
    }
}
