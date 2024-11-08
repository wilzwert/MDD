package com.openclassrooms.mdd.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Wilhelm Zwertvaegher
 * DTO for error responses
 */
@Data
@Schema(description = "Common error object")
public class ErrorResponseDto {
    @Schema(description = "Http status code")
    private String status;
    @Schema(description = "Error message")
    private String message;
    @Schema(description = "Error date and time")
    private String time;
}