package com.openclassrooms.mdd.security.service;


import com.openclassrooms.mdd.model.RefreshToken;
import com.openclassrooms.mdd.model.User;
import com.openclassrooms.mdd.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:15/11/2024
 * Time:10:18
 */

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {
    private final long JWT_REFRESH_EXPIRATION = 86400000;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(refreshTokenService, "jwtRefreshExpiration", JWT_REFRESH_EXPIRATION);
    }

    @Nested
    class GetRefreshToken {
        @Test
        public void shouldReturnEmptyOptionalWhenRefreshTokenNotFoundByToken() {
            when(refreshTokenRepository.findByToken("refresh_token")).thenReturn(Optional.empty());

            assertThat(refreshTokenService.findByToken("refresh_token")).isEmpty();

            verify(refreshTokenRepository).findByToken("refresh_token");
        }

        @Test
        public void shouldReturnRefreshTokenWhenRefreshTokenFoundByToken() {
            RefreshToken refreshToken = new RefreshToken().setId(1).setToken("refresh_token");
            when(refreshTokenRepository.findByToken("refresh_token")).thenReturn(Optional.of(refreshToken));

            assertThat(refreshTokenService.findByToken("refresh_token")).isPresent().get().isEqualTo(refreshToken);

            verify(refreshTokenRepository).findByToken("refresh_token");
        }

        @Test
        public void shouldReturnEmptyOptionalWhenRefreshTokenNotFoundByTokenAndUser() {
            User user = new User().setId(1);
            when(refreshTokenRepository.findByTokenAndUser("refresh_token", user)).thenReturn(Optional.empty());

            assertThat(refreshTokenService.findByTokenAndUser("refresh_token", user)).isEmpty();

            verify(refreshTokenRepository).findByTokenAndUser("refresh_token", user);
        }

        @Test
        public void shouldReturnRefreshTokenWhenRefreshTokenFoundByTokenAndUser() {
            User user = new User().setId(1);
            RefreshToken refreshToken = new RefreshToken().setId(1).setToken("refresh_token").setUser(user);
            when(refreshTokenRepository.findByTokenAndUser("refresh_token", user)).thenReturn(Optional.of(refreshToken));

            assertThat(refreshTokenService.findByTokenAndUser("refresh_token", user)).isPresent().get().isEqualTo(refreshToken);

            verify(refreshTokenRepository).findByTokenAndUser("refresh_token", user);
        }
    }

    @Nested
    class VerifyRefreshTokenExpiration {
        @Test
        public void shouldReturnTrueWhenRefreshTokenNotExpired() {
            RefreshToken refreshToken = new RefreshToken().setId(1).setToken("refresh_token").setExpiryDate(Instant.now().plusSeconds(100));

            assertThat(refreshTokenService.verifyExpiration(refreshToken)).isTrue();
        }

        @Test
        public void shouldReturnFalseWhenRefreshTokenExpired() {
            RefreshToken refreshToken = new RefreshToken().setId(1).setToken("refresh_token").setExpiryDate(Instant.now().minusSeconds(100));
            doNothing().when(refreshTokenRepository).delete(refreshToken);

            assertThat(refreshTokenService.verifyExpiration(refreshToken)).isFalse();
            verify(refreshTokenRepository).delete(refreshToken);
        }
    }

    @Nested
    class GetOrCreateRefreshToken {
        @Test
        public void shouldGetNonExpiredRefreshToken() {
            Instant now = Instant.now();
            User user = new User().setId(1);
            RefreshToken refreshToken = new RefreshToken().setId(1).setToken("refresh_token").setExpiryDate(now.plusSeconds(100));
            when(refreshTokenRepository.findByUser(user)).thenReturn(Optional.of(refreshToken));

            RefreshToken updatedRefreshToken = refreshTokenService.getOrCreateRefreshToken(user);

            assertThat(updatedRefreshToken).isNotNull();
            assertThat(updatedRefreshToken.getId()).isEqualTo(refreshToken.getId());

            verify(refreshTokenRepository, never()).delete(refreshToken);
            verify(refreshTokenRepository, never()).save(refreshToken);
        }

        @Test
        public void shouldDeleteExpiredRefreshTokenAndCreateNewRefreshToken() {
            Instant now = Instant.now();
            User user = new User().setId(1);
            RefreshToken refreshToken = new RefreshToken().setId(1).setToken("refresh_token").setExpiryDate(Instant.now().minusSeconds(100));

            when(refreshTokenRepository.findByUser(user)).thenReturn(Optional.of(refreshToken));
            doNothing().when(refreshTokenRepository).delete(refreshToken);
            when(refreshTokenRepository.save(any(refreshToken.getClass()))).thenAnswer(i -> i.getArgument(0));

            RefreshToken newRefreshToken = refreshTokenService.getOrCreateRefreshToken(user);

            assertThat(newRefreshToken).isNotNull().isNotEqualTo(refreshToken);
            assertThat(newRefreshToken.getId()).isNotEqualTo(refreshToken.getId());
            assertThat(newRefreshToken.getExpiryDate()).isAfterOrEqualTo(now.plusMillis(JWT_REFRESH_EXPIRATION));
            verify(refreshTokenRepository).delete(refreshToken);
            verify(refreshTokenRepository).save(any(refreshToken.getClass()));
        }

        @Test
        public void shouldCreateNewRefreshToken() {
            Instant now = Instant.now();
            User user = new User().setId(1);
            when(refreshTokenRepository.findByUser(user)).thenReturn(Optional.empty());
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArgument(0));

            RefreshToken newRefreshToken = refreshTokenService.getOrCreateRefreshToken(user);

            assertThat(newRefreshToken).isNotNull();
            assertThat(newRefreshToken.getExpiryDate()).isAfterOrEqualTo(now.plusMillis(JWT_REFRESH_EXPIRATION));
            verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));
            verify(refreshTokenRepository).save(any(RefreshToken.class));
        }
    }
}
