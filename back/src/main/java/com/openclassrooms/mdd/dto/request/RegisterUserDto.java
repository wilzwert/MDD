package com.openclassrooms.mdd.dto.request;


import com.openclassrooms.mdd.validation.Password;
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
@Schema(description = "Object expected for a user registration request" )
public class RegisterUserDto {
    @NotBlank(message = "The email is required")
    @Email(message = "Email should be valid")
    @Schema(description = "User email")
    private String email;

    @NotBlank(message = "The username is required")
    @Schema(description = "User username")
    private String userName;

    @NotBlank(message = "The password is required")
    @Password
    @Schema(description = "User password")
    private String password;
}