package com.openclassrooms.mdd.security.service;


import com.openclassrooms.mdd.model.User;
import com.openclassrooms.mdd.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom UserDetailsService used to retrieve user by username extracted from a jwt token subject
 * As of now, the jwt token subject is the user's id
 *
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:15:58
 */
@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, NumberFormatException {
        // we use the id as jwt token subject, therefore we load the user by its id
        User foundUser = userRepository.findById(Integer.parseInt(username)).orElseThrow(() -> new UsernameNotFoundException(username));
        return new UserDetailsImpl(foundUser.getId(), foundUser.getEmail(), foundUser.getPassword());
    }
}