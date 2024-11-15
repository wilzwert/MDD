package com.openclassrooms.mdd.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author Wilhelm Zwertvaegher
 * Date:15/11/2024
 * Time:09:15
 */

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Schema(description = "Response on jwt token refresh success" )
public class JwtRefreshResponse {
    @Schema(description = "The new JWT token")
    private String token;

    @Schema(description = "The token type")
    private String type = "Bearer";

    @Schema(description = "The associated refresh token")
    private String refreshToken;
}
