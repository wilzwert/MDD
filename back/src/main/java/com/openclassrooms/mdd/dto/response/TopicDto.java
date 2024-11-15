package com.openclassrooms.mdd.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @author Wilhelm Zwertvaegher
 * Date:08/11/2024
 * Time:15:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Representation of a topic" )
public class TopicDto {

    @Schema(description = "Topic id")
    private int id;

    @Schema(description = "Topic title")
    private String title;

    @Schema(description = "Topic description")
    private String description;

    @Schema(description = "Topic creation date")
    private LocalDateTime createdAt;

    @Schema(description = "Topic update date")
    private LocalDateTime updatedAt;
}
