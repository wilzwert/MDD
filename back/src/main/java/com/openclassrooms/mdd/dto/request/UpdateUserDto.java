package com.openclassrooms.mdd.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:16:38
 */

@Data
@Schema(description = "Object expected for a user update request" )
public class UpdateUserDto {
    @NotBlank(message = "The email is required")
    @Email(message = "Email should be valid")
    @Schema(description = "User email")
    private String email;

    @NotBlank(message = "The username is required")
    @Schema(description = "User username")
    private String username;
}