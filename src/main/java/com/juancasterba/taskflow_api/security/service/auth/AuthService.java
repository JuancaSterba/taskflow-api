package com.juancasterba.taskflow_api.security.service.auth;

import com.juancasterba.taskflow_api.security.dto.LoginRequestDTO;
import com.juancasterba.taskflow_api.security.dto.LoginResponseDTO;
import com.juancasterba.taskflow_api.security.dto.RegisterRequestDTO;
import com.juancasterba.taskflow_api.security.dto.RegisterResponseDTO;

public interface AuthService {

    public RegisterResponseDTO registerUser(RegisterRequestDTO dto);
    public LoginResponseDTO login(LoginRequestDTO request);

}
