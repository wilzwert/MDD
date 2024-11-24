package com.openclassrooms.mdd.security.service;


import com.openclassrooms.mdd.model.User;
import com.openclassrooms.mdd.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, NumberFormatException {
        log.info("Loading User details for  {}", username);
        User foundUser = userRepository.findById(Integer.parseInt(username)).orElseThrow(() -> new UsernameNotFoundException(username));
        log.info("User details service got {}", foundUser);
        return new UserDetailsImpl(foundUser.getId(), foundUser.getEmail(), foundUser.getPassword());
    }
}