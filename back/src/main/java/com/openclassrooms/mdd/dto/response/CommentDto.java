package com.openclassrooms.mdd.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Wilhelm Zwertvaegher
 * Date:08/11/2024
 * Time:15:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Representation of a comment on a post" )
public class CommentDto {

    @Schema(description = "Comment id")
    private int id;

    @Schema(description = "Comment author")
    private UserDto author;

    @Schema(description = "Post id")
    private int postId;

    @Schema(description = "Comment content")
    private String content;

    @Schema(description = "Comment creation date")
    private LocalDateTime createdAt;

    @Schema(description = "Comment update date")
    private LocalDateTime updatedAt;
}
