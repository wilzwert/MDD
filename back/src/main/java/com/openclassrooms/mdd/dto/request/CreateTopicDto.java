package com.openclassrooms.mdd.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request DTO used for topic creation
 * @author Wilhelm Zwertvaegher
 * Date:08/11/2024
 * Time:15:28
 */

@Data
@Schema(description = "Object expected for a topic creation request" )
public class CreateTopicDto {

    @NotBlank(message = "The title is required")
    @Schema(description = "Topic title")
    private String title;

    @NotBlank(message = "The description is required")
    @Schema(description = "Topic description")
    private String description;
}
