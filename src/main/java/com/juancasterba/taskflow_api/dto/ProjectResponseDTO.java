package com.juancasterba.taskflow_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents a project with its details and associated tasks")
public class ProjectResponseDTO {

    @Schema(description = "Unique identifier of the project", example = "1")
    private Long id;

    @Schema(description = "Name of the project", example = "New E-commerce Platform")
    private String name;

    @Schema(description = "Detailed description of the project", example = "Development of the new online sales platform for the company.")
    private String description;

    @Schema(description = "Username of the project owner", example = "john.doe")
    private String ownerUsername;

    @Schema(description = "List of tasks associated with the project")
    private List<TaskResponseDTO> tasks;

}
