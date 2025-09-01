package com.juancasterba.taskflow_api.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Data required to register a new user")
public class RegisterRequestDTO {

    @NotBlank(message = "Username is required")
    @Schema(description = "A unique username for the new user", example = "jane.doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Schema(description = "The user's email address", example = "jane.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Schema(description = "A secure password for the user (min 8 characters)", example = "MyStrongPassword456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

}
