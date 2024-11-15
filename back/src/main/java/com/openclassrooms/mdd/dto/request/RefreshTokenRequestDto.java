package com.openclassrooms.mdd.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Wilhelm Zwertvaegher
 * Date:15/11/2024
 * Time:09:14
 */

@Data
public class RefreshTokenRequestDto {

    @JsonProperty("refresh_token")
    @NotBlank
    private String refreshToken;
}
