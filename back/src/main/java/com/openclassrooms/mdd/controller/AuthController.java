package com.openclassrooms.mdd.controller;


import com.openclassrooms.mdd.dto.request.LoginRequestDto;
import com.openclassrooms.mdd.dto.request.RegisterUserDto;
import com.openclassrooms.mdd.dto.response.ErrorResponseDto;
import com.openclassrooms.mdd.dto.response.JwtResponse;
import com.openclassrooms.mdd.mapper.UserMapper;
import com.openclassrooms.mdd.model.User;
import com.openclassrooms.mdd.security.service.JwtService;
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
    private final UserMapper userMapper;

    public AuthController(UserService userService, JwtService jwtService, UserMapper userMapper) {
        this.userService = userService;
        this.jwtService = jwtService;
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
        log.info("Register user with email {} and userName {}", registerUserDto.getEmail(), registerUserDto.getUserName());
        User registerUser = userMapper.registerUserDtoToUser(registerUserDto);
        try {
            User user = userService.registerUser(registerUser);
            String token = jwtService.generateToken(user);
            log.info("User with email {} registered with id {}", registerUserDto.getEmail(), user.getId());
            return new JwtResponse(token, user.getId(), user.getUserName());
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
            User user = userService.authenticateUser(loginRequestDto.getEmail(), loginRequestDto.getPassword());
            String token = jwtService.generateToken(user);
            log.info("User with email {} successfully authenticated, sending JWT token", loginRequestDto.getEmail());
            return new JwtResponse(token, user.getId(), user.getUserName());
        }
        catch (AuthenticationException e) {
            log.info("Login failed for User with email {}", loginRequestDto.getEmail());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login failed. "+e.getMessage());
        }
    }


}
