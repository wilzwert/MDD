package com.openclassrooms.mdd.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author Wilhelm Zwertvaegher
 * DTO used to send a confirmation message on Message creation
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Schema(description = "Response message" )
public class MessageResponseDto {
    @Schema(description = "The message")
    private String message;
}
