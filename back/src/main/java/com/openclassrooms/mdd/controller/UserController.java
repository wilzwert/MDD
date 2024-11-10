package com.openclassrooms.mdd.controller;


import com.openclassrooms.mdd.dto.response.UserDto;
import com.openclassrooms.mdd.mapper.UserMapper;
import com.openclassrooms.mdd.model.User;
import com.openclassrooms.mdd.security.service.JwtService;
import com.openclassrooms.mdd.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

/**
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:15:51
 */

@RestController
@Slf4j
@SecurityRequirement(name = "Bearer Authentication")
@RequestMapping("/api/user")
@Tag(name = "User endpoints", description = "User info endpoints")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, JwtService jwtService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Operation(summary = "Get current user", description = "Get current user")
    @GetMapping("/me")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User info", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDto.class))
            })
    })
    public UserDto me(Principal principal) {
        log.info("Get current user {} info", principal.getName());
        User user = userService.findUserByEmail(principal.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return userMapper.userToUserDTO(user);
    }

    /*
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
    }*/


}
