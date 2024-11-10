package com.openclassrooms.mdd.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Wilhelm Zwertvaegher
 * Date:08/11/2024
 * Time:15:28
 */

@Data
@Schema(description = "Object expected for a comment creation or update request" )
public class CreateOrUpdateCommentDto {

    @NotBlank(message = "The content is required")
    @Schema(description = "Comment content")
    private String content;
}
