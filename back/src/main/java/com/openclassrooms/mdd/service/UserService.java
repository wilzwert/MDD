package com.openclassrooms.mdd.service;


import com.openclassrooms.mdd.model.Subscription;
import com.openclassrooms.mdd.model.User;
import org.springframework.security.core.AuthenticationException;

import java.util.List;
import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:16:29
 */
public interface UserService {
    User registerUser(User user);
    User authenticateUser(String email, String password) throws AuthenticationException;

    Optional<User> findUserByEmail(String email);
    String encodePassword(String password);
    void deleteUser(User user);
    Subscription subscribe(User user, int topicId);
    void unSubscribe(User user, int topicId);
    List<Subscription> getSubscriptionsByUser(User user);
}
