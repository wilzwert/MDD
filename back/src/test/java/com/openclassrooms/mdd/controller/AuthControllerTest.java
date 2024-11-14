package com.openclassrooms.mdd.controller;

import com.openclassrooms.mdd.dto.request.LoginRequestDto;
import com.openclassrooms.mdd.dto.request.RegisterUserDto;
import com.openclassrooms.mdd.dto.response.JwtResponse;
import com.openclassrooms.mdd.mapper.UserMapper;
import com.openclassrooms.mdd.model.User;
import com.openclassrooms.mdd.security.service.JwtService;
import com.openclassrooms.mdd.service.UserService;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
    private UserMapper userMapper;

    @Test
    public void shouldThrowConflictResponseStatusExceptionOnRegisterWhenEmailAlreadyExists() {
        RegisterUserDto registerUserDto = new RegisterUserDto();
        registerUserDto.setUserName("test");
        registerUserDto.setEmail("test@example.com");

        when(userMapper.registerUserDtoToUser(registerUserDto)).thenReturn(new User().setUserName("test").setEmail("test@example.com"));
        when(userService.registerUser(any(User.class))).thenThrow(new EntityExistsException("A user already registered with this email"));

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

        when(userMapper.registerUserDtoToUser(registerUserDto)).thenReturn(user);
        when(userService.registerUser(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("jwt_token");

        JwtResponse jwtResponse = authController.register(registerUserDto);
        assertThat(jwtResponse.getId()).isEqualTo(1);
        assertThat(jwtResponse.getToken()).isEqualTo("jwt_token");
        assertThat(jwtResponse.getType()).isEqualTo("Bearer");
        assertThat(jwtResponse.getUsername()).isEqualTo("test");
    }

    @Test
    public void shouldThrowUnauthorizedResponseStatusExceptionOnRegisterWhenLoginFailed() {
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

        when(userService.authenticateUser("test@example.com", "Abcd!1234")).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("jwt_token");

        JwtResponse jwtResponse = authController.login(loginRequestDto);

        assertThat(jwtResponse.getId()).isEqualTo(1);
        assertThat(jwtResponse.getToken()).isEqualTo("jwt_token");
        assertThat(jwtResponse.getType()).isEqualTo("Bearer");
        assertThat(jwtResponse.getUsername()).isEqualTo("test");
    }
}
