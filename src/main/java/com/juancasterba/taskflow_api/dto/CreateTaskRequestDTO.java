package com.juancasterba.taskflow_api.dto;

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
public class CreateTaskRequestDTO {

    @NotBlank(message = "Task title cannot be blank")
    @Size(max = 100, message = "Task title must be less than 100 characters")
    private String title;

    @Size(max = 500, message = "Task description must be less than 500 characters")
    private String description;

    @Default
    private boolean completed = false;

}
