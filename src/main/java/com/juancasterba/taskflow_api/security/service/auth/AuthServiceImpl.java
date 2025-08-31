package com.juancasterba.taskflow_api.security.service.auth;

import com.juancasterba.taskflow_api.security.dto.LoginRequestDTO;
import com.juancasterba.taskflow_api.security.dto.LoginResponseDTO;
import com.juancasterba.taskflow_api.security.dto.RegisterRequestDTO;
import com.juancasterba.taskflow_api.security.dto.RegisterResponseDTO;
import com.juancasterba.taskflow_api.security.mapper.UserMapper;
import com.juancasterba.taskflow_api.security.model.User;
import com.juancasterba.taskflow_api.security.repository.UserRepository;
import com.juancasterba.taskflow_api.security.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public RegisterResponseDTO registerUser(RegisterRequestDTO dto) {
        User user = UserMapper.toUserEntity(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return UserMapper.toUserResponseDTO(savedUser);
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtService.generateToken(user);
        return new LoginResponseDTO(token);
    }

}
