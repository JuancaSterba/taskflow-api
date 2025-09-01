package com.juancasterba.taskflow_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data required to create or update a task")
public class CreateTaskRequestDTO {

    @NotBlank(message = "Task title cannot be blank")
    @Size(max = 100, message = "Task title must be less than 100 characters")
    @Schema(description = "Title of the task", example = "Implement user authentication", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Size(max = 500, message = "Task description must be less than 500 characters")
    @Schema(description = "Detailed description of the task", example = "Set up JWT-based authentication for the API.")
    private String description;

    @Schema(description = "Indicates whether the task is completed or not", example = "false")
    @Default
    private boolean completed = false;

}
