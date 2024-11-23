package com.openclassrooms.mdd.controller;

import com.openclassrooms.mdd.dto.request.LoginRequestDto;
import com.openclassrooms.mdd.dto.request.RefreshTokenRequestDto;
import com.openclassrooms.mdd.dto.request.RegisterUserDto;
import com.openclassrooms.mdd.dto.response.JwtRefreshResponse;
import com.openclassrooms.mdd.dto.response.JwtResponse;
import com.openclassrooms.mdd.mapper.UserMapper;
import com.openclassrooms.mdd.model.RefreshToken;
import com.openclassrooms.mdd.model.User;
import com.openclassrooms.mdd.security.service.JwtService;
import com.openclassrooms.mdd.security.service.RefreshTokenService;
import com.openclassrooms.mdd.service.UserService;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Wilhelm Zwertvaegher
 * Date:05/11/2024
 * Time:11:59
 */
@Tag("Post")
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserMapper userMapper;

    @Nested
    class RegisterTest {

        @Test
        public void shouldThrowConflictResponseStatusExceptionOnRegisterWhenEmailAlreadyExists() {
            RegisterUserDto registerUserDto = new RegisterUserDto();
            registerUserDto.setUserName("test");
            registerUserDto.setEmail("test@example.com");

            when(userMapper.registerUserDtoToUser(registerUserDto)).thenReturn(new User().setUserName("test").setEmail("test@example.com"));
            when(userService.registerUser(any(User.class))).thenThrow(new EntityExistsException("Email already exists"));

            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authController.register(registerUserDto));
            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        }

        @Test
        public void shouldThrowConflictResponseStatusExceptionOnRegisterWhenUserNameAlreadyExists() {
            RegisterUserDto registerUserDto = new RegisterUserDto();
            registerUserDto.setUserName("test");
            registerUserDto.setEmail("test@example.com");

            when(userMapper.registerUserDtoToUser(registerUserDto)).thenReturn(new User().setUserName("test").setEmail("test@example.com"));
            when(userService.registerUser(any(User.class))).thenThrow(new EntityExistsException("Username already exists"));

            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authController.register(registerUserDto));
            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        }

        @Test
        public void shouldReturnJwtResponseOnRegisterSuccess() {
            RegisterUserDto registerUserDto = new RegisterUserDto();
            registerUserDto.setUserName("test");
            registerUserDto.setEmail("test@example.com");
            registerUserDto.setPassword("Abcd!1234");
            User user = new User().setId(1).setUserName("test").setEmail("test@example.com").setPassword("Abcd!1234");
            Instant refreshTokenExpiry = Instant.now().plusSeconds(86400);

            when(userMapper.registerUserDtoToUser(registerUserDto)).thenReturn(user);
            when(userService.registerUser(any(User.class))).thenReturn(user);
            when(jwtService.generateToken(user)).thenReturn("jwt_token");
            when(refreshTokenService.getOrCreateRefreshToken(user)).thenReturn(new RefreshToken().setId(1).setUser(user).setToken("refresh_token").setExpiryDate(refreshTokenExpiry));

            JwtResponse jwtResponse = authController.register(registerUserDto);
            assertThat(jwtResponse.getId()).isEqualTo(1);
            assertThat(jwtResponse.getToken()).isEqualTo("jwt_token");
            assertThat(jwtResponse.getType()).isEqualTo("Bearer");
            assertThat(jwtResponse.getUsername()).isEqualTo("test");
            assertThat(jwtResponse.getRefreshToken()).isEqualTo("refresh_token");
        }
    }

    @Nested
    class LoginTest {
        @Test
        public void shouldThrowUnauthorizedResponseStatusExceptionWhenLoginFailed() {
            LoginRequestDto loginRequestDto = new LoginRequestDto();
            loginRequestDto.setEmail("test@example.com");
            loginRequestDto.setPassword("Abcd!1234");

            when(userService.authenticateUser("test@example.com", "Abcd!1234")).thenThrow(new AuthenticationException("User not found"){
                @Override
                public String getMessage() {
                    return super.getMessage();
                }
            });

            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authController.login(loginRequestDto));
            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        public void shouldReturnJwtResponseOnLoginSuccess() {
            LoginRequestDto loginRequestDto = new LoginRequestDto();
            loginRequestDto.setEmail("test@example.com");
            loginRequestDto.setPassword("Abcd!1234");

            User user = new User().setId(1).setUserName("test").setEmail("test@example.com");
            Instant refreshTokenExpiry = Instant.now().plusSeconds(86400);

            when(userService.authenticateUser("test@example.com", "Abcd!1234")).thenReturn(user);
            when(jwtService.generateToken(user)).thenReturn("jwt_token");
            when(refreshTokenService.getOrCreateRefreshToken(user)).thenReturn(new RefreshToken().setId(1).setUser(user).setToken("refresh_token").setExpiryDate(refreshTokenExpiry));

            JwtResponse jwtResponse = authController.login(loginRequestDto);

            assertThat(jwtResponse.getId()).isEqualTo(1);
            assertThat(jwtResponse.getToken()).isEqualTo("jwt_token");
            assertThat(jwtResponse.getType()).isEqualTo("Bearer");
            assertThat(jwtResponse.getUsername()).isEqualTo("test");
            assertThat(jwtResponse.getRefreshToken()).isEqualTo("refresh_token");
        }
    }

    @Nested
    class RefreshTokenTest {
        @Test
        public void shouldThrowUnauthorizedResponseStatusExceptionWhenRefreshTokenNotFound() {
            RefreshTokenRequestDto refreshTokenRequestDto = new RefreshTokenRequestDto();
            refreshTokenRequestDto.setRefreshToken("refresh_token");

            when(refreshTokenService.findByToken("refresh_token")).thenReturn(Optional.empty());

            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authController.refreshToken(refreshTokenRequestDto));

            verify(refreshTokenService).findByToken("refresh_token");
            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(exception.getReason()).isEqualTo("Refresh token not found");
        }

        @Test
        public void shouldThrowUnauthorizedResponseStatusExceptionWhenRefreshTokenExpired() {
            RefreshTokenRequestDto refreshTokenRequestDto = new RefreshTokenRequestDto();
            refreshTokenRequestDto.setRefreshToken("refresh_token");

            RefreshToken refreshToken = new RefreshToken().setToken("refresh_token").setExpiryDate(Instant.now().minusSeconds(100));
            when(refreshTokenService.findByToken("refresh_token")).thenReturn(Optional.of(refreshToken));
            when(refreshTokenService.verifyExpiration(refreshToken)).thenReturn(false);

            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authController.refreshToken(refreshTokenRequestDto));
            verify(refreshTokenService).findByToken("refresh_token");
            verify(refreshTokenService).verifyExpiration(refreshToken);
            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(exception.getReason()).isEqualTo("Refresh token expired, please login again");
        }

        @Test
        public void shouldReturnJwtRefreshResponseOnRefreshSuccess() {
            RefreshTokenRequestDto refreshTokenRequestDto = new RefreshTokenRequestDto();
            refreshTokenRequestDto.setRefreshToken("refresh_token");

            User user = new User().setId(1).setUserName("test").setEmail("test@example.com");
            RefreshToken refreshToken = new RefreshToken().setUser(user).setToken("refresh_token").setExpiryDate(Instant.now().plusSeconds(86400));

            when(refreshTokenService.findByToken("refresh_token")).thenReturn(Optional.of(refreshToken));
            when(refreshTokenService.verifyExpiration(refreshToken)).thenReturn(true);
            when(jwtService.generateToken(user)).thenReturn("jwt_token");

            JwtRefreshResponse jwtRefreshResponse = authController.refreshToken(refreshTokenRequestDto);

            verify(refreshTokenService).findByToken("refresh_token");
            verify(refreshTokenService).verifyExpiration(refreshToken);
            verify(jwtService).generateToken(user);
            assertThat(jwtRefreshResponse).isNotNull();
            assertThat(jwtRefreshResponse.getRefreshToken()).isEqualTo("refresh_token");
            assertThat(jwtRefreshResponse.getToken()).isEqualTo("jwt_token");
            assertThat(jwtRefreshResponse.getType()).isEqualTo("Bearer");
        }
    }
}
