package com.juancasterba.taskflow_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard structure for API error responses")
public class ErrorResponseDTO {

    @Schema(description = "Timestamp when the error occurred", example = "2023-10-27T10:30:00.123Z")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code", example = "404")
    private int status;

    @Schema(description = "HTTP status error name", example = "Not Found")
    private String error;

    @Schema(description = "A specific and readable message about the error", example = "Project with ID 99 not found.")
    private String message;

    @Schema(description = "The URL path where the error occurred", example = "/api/v1/projects/99")
    private String path;
}
