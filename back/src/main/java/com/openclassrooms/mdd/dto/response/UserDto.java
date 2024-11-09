package com.openclassrooms.mdd.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * DTO used to send a User in a http response
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Representation of a user" )
public class UserDto {
    private int id;

    @Schema(description = "User email")
    private String email;

    @Schema(description = "User username")
    private String userName;

    @JsonProperty("created_at")
    @Schema(description = "User creation date")
    private String createdAt;

    @JsonProperty("updated_at")
    @Schema(description = "User update date")
    private String updatedAt;
}
