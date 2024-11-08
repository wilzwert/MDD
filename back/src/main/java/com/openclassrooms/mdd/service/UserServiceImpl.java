package com.openclassrooms.mdd.service;


import com.openclassrooms.mdd.model.User;
import com.openclassrooms.mdd.repository.UserRepository;
import com.openclassrooms.mdd.security.service.JwtService;
import jakarta.persistence.EntityExistsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:16:32
 */
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    // TODO private final AclService aclService;

    public UserServiceImpl(
            final UserRepository userRepository,
            final PasswordEncoder passwordEncoder,
            final AuthenticationManager authenticationManager,
            // TODO final AclService aclService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        // this.aclService = aclService;
    }

    @Override
    public User registerUser(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if(existingUser.isPresent()) {
            throw new EntityExistsException("A user already registered with this email");
        }
        user.setPassword(encodePassword(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(User user) {
        // TODO aclService.removeUser(user);
        userRepository.delete(user);
    }

    @Override
    public User authenticateUser(String email, String password) throws AuthenticationException {
        Optional<User> user = findUserByEmail(email);
        if(user.isEmpty()) {
            throw new AuthenticationException("User not found") {
                @Override
                public String getMessage() {
                    return super.getMessage();
                }
            };
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        return user.get();
    }

    @Override
    public Optional<User> findUserByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findUserById(final int id) {
        return userRepository.findById(id);
    }

    @Override
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
