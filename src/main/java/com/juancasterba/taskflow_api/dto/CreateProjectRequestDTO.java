package com.juancasterba.taskflow_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data required to create or update a project")
public class CreateProjectRequestDTO {

    @NotBlank(message = "Project name cannot be blank")
    @Size(max = 100, message = "Project name must be less than 100 characters")
    @Schema(description = "Name of the project", example = "New E-commerce Platform", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Size(max = 500, message = "Project description must be less than 500 characters")
    @Schema(description = "Detailed description of the project", example = "Development of the new online sales platform for the company.")
    private String description;

}
