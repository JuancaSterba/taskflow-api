package com.juancasterba.taskflow_api.security.controller.auth;

import com.juancasterba.taskflow_api.security.dto.LoginRequestDTO;
import com.juancasterba.taskflow_api.security.dto.LoginResponseDTO;
import com.juancasterba.taskflow_api.security.dto.RegisterRequestDTO;
import com.juancasterba.taskflow_api.security.dto.RegisterResponseDTO;
import com.juancasterba.taskflow_api.security.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> registerUser(@Valid @RequestBody RegisterRequestDTO dto){
        RegisterResponseDTO user = authService.registerUser(dto);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

}
