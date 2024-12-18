package com.openclassrooms.mdd.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO used for posts
 * @author Wilhelm Zwertvaegher
 * Date:08/11/2024
 * Time:15:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Representation of a post" )
public class PostDto {

    @Schema(description = "Post id")
    private int id;

    @Schema(description = "Post title")
    private String title;

    @Schema(description = "Post content")
    private String content;

    @Schema(description = "Post creation date")
    private LocalDateTime createdAt;

    @Schema(description = "Post update date")
    private LocalDateTime updatedAt;

    @Schema(description = "The post author")
    private UserDto author;

    @Schema(description = "The post topic")
    private TopicDto topic;
}
