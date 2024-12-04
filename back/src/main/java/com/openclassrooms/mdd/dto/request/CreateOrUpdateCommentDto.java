package com.openclassrooms.mdd.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO used for comment creation or update request
 * Please note : as of now, only comment creation is supported by the API
 * @author Wilhelm Zwertvaegher
 * Date:08/11/2024
 * Time:15:28
 */

@Data
@Schema(description = "Object expected for a comment creation or update request" )
public class CreateOrUpdateCommentDto {

    @NotBlank(message = "The content is required")
    @Size(min = 5, message = "Content too short")
    @Schema(description = "Comment content")
    private String content;
}
