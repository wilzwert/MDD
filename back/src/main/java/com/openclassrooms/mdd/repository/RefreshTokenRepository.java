package com.openclassrooms.mdd.repository;


import com.openclassrooms.mdd.model.RefreshToken;
import com.openclassrooms.mdd.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:15/11/2024
 * Time:09:17
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByTokenAndUser(String token, User user);
    Optional<RefreshToken> findByUser(User user);
}
