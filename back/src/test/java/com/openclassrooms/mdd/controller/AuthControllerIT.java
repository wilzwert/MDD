package com.openclassrooms.mdd.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.mdd.dto.request.LoginRequestDto;
import com.openclassrooms.mdd.dto.request.RefreshTokenRequestDto;
import com.openclassrooms.mdd.dto.request.RegisterUserDto;
import com.openclassrooms.mdd.dto.response.JwtResponse;
import com.openclassrooms.mdd.model.RefreshToken;
import com.openclassrooms.mdd.model.User;
import com.openclassrooms.mdd.repository.RefreshTokenRepository;
import com.openclassrooms.mdd.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.time.Instant;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Wilhelm Zwertvaegher
 * Date:05/11/2024
 * Time:10:53
 */

@SpringBootTest
@AutoConfigureMockMvc
@Tag("Integration")
public class AuthControllerIT {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final static String LOGIN_URL = "/api/auth/login";
    private final static String REGISTER_URL = "/api/auth/register";
    private final static String REFRESH_TOKEN_URL = "/api/auth/refreshToken";

    @Nested
    class AuthControllerAuthenticateIT {

        @Test
        public void shouldReturnBadRequestWhenRequestBodyEmpty() throws Exception {
            mockMvc.perform(post(LOGIN_URL))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenRequestBodyInvalid() throws Exception {
            // no password
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setEmail("test@example.com");
            mockMvc.perform(post(LOGIN_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnUnauthorizedWhenUserNotFound() throws Exception {
            // no password
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setEmail("test@example.com");
            loginRequest.setPassword("abcd1234");
            mockMvc.perform(post(LOGIN_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        public void shouldReturnJwtResponse() throws Exception {
            // no password
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setEmail("test@example.com");
            loginRequest.setPassword("abcd1234");

            User user = new User()
                    .setId(1)
                    .setEmail("test@example.com")
                    .setPassword(passwordEncoder.encode("abcd1234"))
                    .setUsername("testuser");

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(userRepository.findById(1)).thenReturn(Optional.of(user));
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArgument(0));

            MvcResult result = mockMvc.perform(post(LOGIN_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andReturn();
            String json = result.getResponse().getContentAsString();
            JwtResponse response = objectMapper.readValue(json, JwtResponse.class);
            assertThat(response.getType()).isEqualTo("Bearer");
            assertThat(response.getToken()).isNotEmpty();
            assertThat(response.getRefreshToken()).isNotEmpty();
            assertThat(response.getId()).isEqualTo(1);
            assertThat(response.getUsername()).isEqualTo("testuser");
        }
    }

    @Nested
    class AuthControllerRegisterIT {

        private RegisterUserDto registerUserDto;

        @BeforeEach
        public void setup() {
            // setup a default valid signup request
            registerUserDto = new RegisterUserDto();
            registerUserDto.setEmail("test@example.com");
            registerUserDto.setUsername("testuser");
            registerUserDto.setPassword("aBCd.1234");
        }

        @Test
        public void shouldReturnBadRequestWhenRequestBodyEmpty() throws Exception {
            mockMvc.perform(post(REGISTER_URL))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenEmailEmpty() throws Exception {
            registerUserDto.setEmail("");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenEmailInvalid() throws Exception {
            registerUserDto.setEmail("test");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenUsernameEmpty() throws Exception {
            registerUserDto.setUsername("");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenPasswordEmpty() throws Exception {
            registerUserDto.setPassword("");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenPasswordTooShort() throws Exception {
            registerUserDto.setPassword("pass");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenPasswordWithNoUppercase() throws Exception {
            registerUserDto.setPassword("abcd1234.");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenPasswordWithNoLowercase() throws Exception {
            registerUserDto.setPassword("ABCD1234.");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenPasswordWithNoSpecialChar() throws Exception {
            registerUserDto.setPassword("abCD1234");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenUserWithEmailAlreadyExists() throws Exception {
            User existingUser = new User();
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));

            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserDto)))
                    .andExpect(status().is(HttpStatus.CONFLICT.value()))
                    .andExpect(jsonPath("message").value("Email already exists"));
        }

        @Test
        public void shouldReturnBadRequestWhenUserWithUsernameAlreadyExists() throws Exception {
            User existingUser = new User();
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(existingUser));

            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserDto)))
                    .andExpect(status().is(HttpStatus.CONFLICT.value()))
                    .andExpect(jsonPath("message").value("Username already exists"));
        }

        @Test
        public void shouldRegisterUser() throws Exception {
            when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArgument(0));

            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registerUserDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("token").isNotEmpty())
                    .andExpect(jsonPath("refreshToken").isNotEmpty());


            verify(userRepository).save(any(User.class));
        }
    }

    @Nested
    class AuthControllerRefreshTokenIT {
        @Test
        public void shouldReturnUnauthorizedWhenRefreshTokenNotFound() throws Exception {
            RefreshTokenRequestDto refreshTokenRequestDto = new RefreshTokenRequestDto();
            refreshTokenRequestDto.setRefreshToken("refresh_token");

            when(refreshTokenRepository.findByToken("refresh_token")).thenReturn(Optional.empty());

            mockMvc.perform(post(REFRESH_TOKEN_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(refreshTokenRequestDto)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("message").value("Refresh token not found"));
        }

        @Test
        public void shouldReturnUnauthorizedWhenRefreshTokenExpired() throws Exception {
            RefreshTokenRequestDto refreshTokenRequestDto = new RefreshTokenRequestDto();
            refreshTokenRequestDto.setRefreshToken("refresh_token");

            RefreshToken refreshToken = new RefreshToken().setToken("refresh_token").setExpiryDate(Instant.now().minusSeconds(100));
            when(refreshTokenRepository.findByToken("refresh_token")).thenReturn(Optional.of(refreshToken));

            mockMvc.perform(post(REFRESH_TOKEN_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(refreshTokenRequestDto)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("message").value("Refresh token expired, please login again"));
        }

        @Test
        public void shouldReturnJwtRefreshResponseOnRefreshSuccess() throws Exception {
            RefreshTokenRequestDto refreshTokenRequestDto = new RefreshTokenRequestDto();
            refreshTokenRequestDto.setRefreshToken("refresh_token");

            User user = new User().setId(1).setUsername("test").setEmail("test@example.com");
            RefreshToken refreshToken = new RefreshToken().setUser(user).setToken("refresh_token").setExpiryDate(Instant.now().plusSeconds(86400));

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(refreshTokenRepository.findByToken("refresh_token")).thenReturn(Optional.of(refreshToken));

            mockMvc.perform(post(REFRESH_TOKEN_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(refreshTokenRequestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("token").isNotEmpty())
                    .andExpect(jsonPath("refreshToken").value("refresh_token"))
                    .andExpect(jsonPath("type").value("Bearer"));
        }
    }
}
