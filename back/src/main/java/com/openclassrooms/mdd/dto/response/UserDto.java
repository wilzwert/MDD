package com.openclassrooms.mdd.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
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

    @Schema(description = "User creation date")
    private LocalDateTime createdAt;

    @Schema(description = "User update date")
    private LocalDateTime updatedAt;

    @Schema(description = "User subscriptions")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<SubscriptionDto> subscriptions;
}
