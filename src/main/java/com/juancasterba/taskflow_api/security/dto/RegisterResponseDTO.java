package com.juancasterba.taskflow_api.security.dto;

import lombok.Data;

@Data
public class RegisterResponseDTO {

    private Long id;
    private String username;
    private String email;

}
