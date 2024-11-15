package com.openclassrooms.mdd.controller;


import com.openclassrooms.mdd.dto.request.LoginRequestDto;
import com.openclassrooms.mdd.dto.request.RefreshTokenRequestDto;
import com.openclassrooms.mdd.dto.request.RegisterUserDto;
import com.openclassrooms.mdd.dto.response.ErrorResponseDto;
import com.openclassrooms.mdd.dto.response.JwtRefreshResponse;
import com.openclassrooms.mdd.dto.response.JwtResponse;
import com.openclassrooms.mdd.mapper.UserMapper;
import com.openclassrooms.mdd.model.RefreshToken;
import com.openclassrooms.mdd.model.User;
import com.openclassrooms.mdd.security.service.JwtService;
import com.openclassrooms.mdd.security.service.RefreshTokenService;
import com.openclassrooms.mdd.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityExistsException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:15:51
 */

@RestController
@Slf4j
@RequestMapping("/api/auth")
@Tag(name = "Authentication and registration", description = "Authentication and user registration endpoints")
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserMapper userMapper;

    public AuthController(UserService userService, JwtService jwtService, RefreshTokenService refreshTokenServicen, UserMapper userMapper) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenServicen;
        this.userMapper = userMapper;
    }

    @Operation(summary = "Register new user", description = "Register new user")
    @PostMapping("/register")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registration succeeded", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = JwtResponse.class))
            }),
            @ApiResponse(responseCode = "409", description = "Email already exists", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))
            })
    })
    public JwtResponse register(@Valid @RequestBody final RegisterUserDto registerUserDto) {
        log.info("Register user with {}", registerUserDto);
        User registerUser = userMapper.registerUserDtoToUser(registerUserDto);
        try {
            User user = userService.registerUser(registerUser);
            String token = jwtService.generateToken(user);
            log.info("User with email {} registered with id {}", registerUserDto.getEmail(), user.getId());
            return new JwtResponse(token, "Bearer", refreshTokenService.getOrCreateRefreshToken(user).getToken(), user.getId(), user.getUserName());
        }
        catch (EntityExistsException e) {
            log.warn("Email {} already exists", registerUserDto.getEmail());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
    }

    @Operation(summary = "Login", description = "Get an access token")
    @PostMapping("/login")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login succeeded", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = JwtResponse.class))
            }),
            @ApiResponse(responseCode = "401", description = "Login failed", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))
            })
    })
    public JwtResponse login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        log.info("User login with email {}", loginRequestDto.getEmail());
        try {
            log.info("User login - authenticating");
            User user = userService.authenticateUser(loginRequestDto.getEmail(), loginRequestDto.getPassword());
            log.info("User login - generating token");
            String token = jwtService.generateToken(user);
            log.info("User with email {} successfully authenticated, sending JWT token", loginRequestDto.getEmail());
            return new JwtResponse(token, "Bearer", refreshTokenService.getOrCreateRefreshToken(user).getToken(), user.getId(), user.getUserName());
        }
        catch (AuthenticationException e) {
            log.info("Login failed for User with email {}", loginRequestDto.getEmail());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login failed. "+e.getMessage());
        }
    }

    @Operation(summary = "Refresh access token", description = "Get a new access token")
    @PostMapping("/refreshToken")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Refresh succeeded", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = JwtRefreshResponse.class))
            }),
            @ApiResponse(responseCode = "401", description = "Refresh failed", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))
            })
    })
    public JwtRefreshResponse refreshToken(@Valid @RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
        Optional<RefreshToken> foundRefreshToken = refreshTokenService.findByToken(refreshTokenRequestDto.getRefreshToken());
        if(foundRefreshToken.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token not found");
        }

        if(!refreshTokenService.verifyExpiration(foundRefreshToken.get())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired, please login again");
        }
        RefreshToken refreshToken = foundRefreshToken.get();
        User user = refreshToken.getUser();
        String token = jwtService.generateToken(user);

        return new JwtRefreshResponse(token, "Bearer", refreshToken.getToken());
    }
}