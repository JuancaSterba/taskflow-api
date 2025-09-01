package com.juancasterba.taskflow_api.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Response containing the details of a newly registered user")
public class RegisterResponseDTO {

    @Schema(description = "Unique identifier of the registered user", example = "25")
    private Long id;

    @Schema(description = "Username of the registered user", example = "jane.doe")
    private String username;

    @Schema(description = "Email address of the registered user", example = "jane.doe@example.com")
    private String email;

}
