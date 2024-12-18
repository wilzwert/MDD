package com.openclassrooms.mdd.service;


import com.openclassrooms.mdd.model.Subscription;
import com.openclassrooms.mdd.model.Topic;
import com.openclassrooms.mdd.model.User;
import com.openclassrooms.mdd.repository.SubscriptionRepository;
import com.openclassrooms.mdd.repository.TopicRepository;
import com.openclassrooms.mdd.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:16:32
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserServiceImpl(
            final UserRepository userRepository,
            final TopicRepository topicRepository,
            final SubscriptionRepository subscriptionRepository,
            final PasswordEncoder passwordEncoder,
            final AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public User registerUser(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if(existingUser.isPresent()) {
            throw new EntityExistsException("Email already exists");
        }
        existingUser = userRepository.findByUsername(user.getUsername());
        if(existingUser.isPresent()) {
            throw new EntityExistsException("Username already exists");
        }
        user.setPassword(encodePassword(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user, User updateUser) throws EntityExistsException {
        // user wants to change email
        if(!user.getEmail().equals(updateUser.getEmail())) {
            Optional<User> existingUser = userRepository.findByEmail(updateUser.getEmail());
            if (existingUser.isPresent()) {
                throw new EntityExistsException("Email already exists");
            }
        }
        // user wants to change username
        if(!user.getUsername().equals(updateUser.getUsername())) {
            log.info("Username changes");
            Optional<User> existingUser = userRepository.findByUsername(updateUser.getUsername());
            if (existingUser.isPresent()) {
                throw new EntityExistsException("Username already exists");
            }
        }

        user.setUsername(updateUser.getUsername());
        user.setEmail(updateUser.getEmail());
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Override
    public Subscription subscribe(User user, int topicId) {
        Topic topic = topicRepository.findById(topicId).orElseThrow(() -> new EntityNotFoundException("Cannot find topic"));

        Subscription subscription = new Subscription();
        subscription.setTopic(topic);
        subscription.setUser(user);

        return subscriptionRepository.save(subscription);
    }

    @Override
    public void unSubscribe(User user, int topicId) {
        Topic topic = topicRepository.findById(topicId).orElseThrow(() -> new EntityNotFoundException("Cannot find topic"));

        List<Subscription> subscriptions = user.getSubscriptions().stream().filter(sub -> sub.getTopic().getId() == topic.getId()).toList();
        if(subscriptions.isEmpty()) {
            throw new EntityNotFoundException("Cannot find subscription");
        }

        subscriptionRepository.delete(subscriptions.getFirst());
    }

    @Override
    public List<Subscription> getSubscriptionsByUser(User user) {
        return user.getSubscriptions();
    }

    @Override
    public User authenticateUser(String email, String password) throws AuthenticationException {
        Optional<User> user = findUserByEmail(email);
        if(user.isEmpty()) {
            log.info("User not found");
            throw new AuthenticationException("User not found") {
                @Override
                public String getMessage() {
                    return super.getMessage();
                }
            };
        }
        log.info("User has been found, let's pass to authenticationManager");
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.get().getId(), password)
        );
        return user.get();
    }

    @Override
    public Optional<User> findUserByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
