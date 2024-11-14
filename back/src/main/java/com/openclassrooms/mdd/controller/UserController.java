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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Optional;

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

    public UserController(UserService userService, UserMapper userMapper) {
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
        return userMapper.userToUserDto(user);
    }

    @Operation(summary = "Delete current user", description = "Deletes current user account and info")
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteCurrentUser(Principal principal) {
        log.info("Delete current User, email is {}", principal.getName());
        Optional<User> foundUser = userService.findUserByEmail(principal.getName());
        if(foundUser.isEmpty()) {
            log.warn("Delete current User failed: no user found for email {}", principal.getName());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        userService.deleteUser(foundUser.get());
        log.info("Current User deleted with email {}", foundUser.get().getEmail());
        return ResponseEntity.noContent().build();
    }
}
