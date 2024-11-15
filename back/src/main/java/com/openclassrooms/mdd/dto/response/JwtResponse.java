package com.openclassrooms.mdd.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Schema(description = "Response on login success with token and user info" )
public class JwtResponse {
  @Schema(description = "The JWT token")
  private String token;

  @Schema(description = "The token type")
  private String type = "Bearer";

  @Schema(description = "The associated refresh token")
  private String refreshToken;

  @Schema(description = "The User id")
  private int id;

  @Schema(description = "The User usename")
  private String username;
}
