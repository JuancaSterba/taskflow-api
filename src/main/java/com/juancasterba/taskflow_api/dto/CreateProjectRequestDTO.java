package com.juancasterba.taskflow_api.dto;

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
public class CreateProjectRequestDTO {

    @NotBlank(message = "Project name cannot be blank")
    @Size(max = 100, message = "Project name must be less than 100 characters")
    private String name;

    @Size(max = 500, message = "Project description must be less than 500 characters")
    private String description;

}
