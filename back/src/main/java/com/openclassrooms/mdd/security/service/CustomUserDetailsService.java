package com.openclassrooms.mdd.security.service;


import com.openclassrooms.mdd.model.User;
import com.openclassrooms.mdd.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:15:58
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User foundUser = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));

        return new UserDetailsImpl(foundUser.getId(), foundUser.getEmail(), foundUser.getPassword());
    }
}