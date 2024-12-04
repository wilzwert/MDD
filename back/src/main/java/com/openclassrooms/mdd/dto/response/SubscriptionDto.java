package com.openclassrooms.mdd.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO used for topics subscriptions
 * @author Wilhelm Zwertvaegher
 * Date:08/11/2024
 * Time:15:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Representation of a subscription to a topic" )
public class SubscriptionDto {

    @Schema(description = "User id")
    private int userId;

    @Schema(description = "Topic")
    private TopicDto topic;

    @Schema(description = "Subscription creation date")
    private LocalDateTime createdAt;
}
