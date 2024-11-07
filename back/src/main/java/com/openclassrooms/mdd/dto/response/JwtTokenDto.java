package com.openclassrooms.mdd.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:16:37
 */

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Schema(description = "JWT token response on login success" )
public class JwtTokenDto {
    @Schema(description = "The JWT token")
    private String token;
}