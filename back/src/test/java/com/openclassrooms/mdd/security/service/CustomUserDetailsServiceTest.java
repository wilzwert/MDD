package com.openclassrooms.mdd.security.service;


import com.openclassrooms.mdd.model.User;
import com.openclassrooms.mdd.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Wilhelm Zwertvaegher
 * Date:08/11/2024
 * Time:09:35
 */

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    public void shouldThrowNumberFormatExceptionWHenUserNotFound() {
        assertThrows(NumberFormatException.class, () -> customUserDetailsService.loadUserByUsername("test@example.com"));
    }

    @Test
    public void shouldThrowUsernameNotFoundExceptionWHenUserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername("1"));
        verify(userRepository).findById(1);
    }

    @Test
    public void shouldLoadUserByUsername() {
        User user = new User()
                .setId(1)
                .setEmail("test@example.com")
                .setUserName("Username")
                .setPassword("password");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        UserDetailsImpl userDetails = (UserDetailsImpl) customUserDetailsService.loadUserByUsername("1");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getId()).isEqualTo(1L);
        assertThat(userDetails.getUsername()).isEqualTo("test@example.com");
        assertThat(userDetails.getPassword()).isEqualTo("password");
        assertThat(userDetails.getAuthorities()).hasSize(0);
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
    }
}
