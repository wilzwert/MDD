package com.openclassrooms.mdd.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Wilhelm Zwertvaegher
 * Date:08/11/2024
 * Time:14:34
 */

@Data
public class LoginRequestDto {

    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
