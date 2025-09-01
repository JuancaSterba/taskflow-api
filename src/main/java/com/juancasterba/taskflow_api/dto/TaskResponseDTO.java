package com.juancasterba.taskflow_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents a task with its details")
public class TaskResponseDTO {

    @Schema(description = "Unique identifier of the task", example = "101")
    private Long id;

    @Schema(description = "Title of the task", example = "Implement user authentication")
    private String title;

    @Schema(description = "Detailed description of the task", example = "Set up JWT-based authentication for the API.")
    private String description;

    @Schema(description = "Indicates whether the task is completed or not", example = "false")
    private boolean completed;

}
