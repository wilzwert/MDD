package com.openclassrooms.mdd.security.service;


import com.openclassrooms.mdd.model.RefreshToken;
import com.openclassrooms.mdd.model.User;
import com.openclassrooms.mdd.repository.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * Date:15/11/2024
 * Time:09:16
 * This service handles Refresh Tokens creation, retrieval and verifying
 */

@Service
@Slf4j
public class RefreshTokenService {
    @Value("${security.jwt.refresh-expiration-time}")
    private long jwtRefreshExpiration;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(final RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public Optional<RefreshToken> findByToken(final String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public Optional<RefreshToken> findByTokenAndUser(final String token, final User user) {
        return refreshTokenRepository.findByTokenAndUser(token, user);
    }

    public RefreshToken getOrCreateRefreshToken(User user) {
        Optional<RefreshToken> foundRefreshToken = refreshTokenRepository.findByUser(user);

        if (foundRefreshToken.isPresent() && verifyExpiration(foundRefreshToken.get())) {
            return foundRefreshToken.get();
        }
        else {
            RefreshToken refreshToken = new RefreshToken().setUser(user).setToken(UUID.randomUUID().toString());
            log.info("setting expiry to {} (now is {}, expiration is {})", Instant.now().plusMillis(jwtRefreshExpiration), Instant.now(), jwtRefreshExpiration);
            refreshToken.setExpiryDate(Instant.now().plusMillis(jwtRefreshExpiration));
            return refreshTokenRepository.save(refreshToken);
        }
    }

    public boolean verifyExpiration(RefreshToken refreshToken) {
        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            return false;
        }
        return true;
    }
}
