package com.openclassrooms.mdd.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Wilhelm Zwertvaegher
 * Date:15/11/2024
 * Time:09:14
 */

@Data
public class RefreshTokenRequestDto {

    @NotBlank
    private String refreshToken;
}
