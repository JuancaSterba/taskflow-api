package com.juancasterba.taskflow_api.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Data required for a user to log in")
public class LoginRequestDTO {

    @Schema(description = "User's registered username", example = "john.doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "User's password", example = "MySecurePassword123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

}
