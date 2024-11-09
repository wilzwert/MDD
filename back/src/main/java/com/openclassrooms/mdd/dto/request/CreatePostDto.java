package com.openclassrooms.mdd.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author Wilhelm Zwertvaegher
 * Date:08/11/2024
 * Time:15:28
 */

@Data
@Schema(description = "Object expected for a post creation request" )
public class CreatePostDto {

    @NotNull(message = "The topic is required")
    @JsonProperty("topic_id")
    @Schema(description = "Topic id" )
    private int topicId;

    @NotBlank(message = "The title is required")
    @Schema(description = "Post title")
    private String title;

    @NotBlank(message = "The content is required")
    @Schema(description = "Post content")
    private String content;
}