package com.juancasterba.taskflow_api.security.mapper;

import com.juancasterba.taskflow_api.security.dto.RegisterRequestDTO;
import com.juancasterba.taskflow_api.security.dto.RegisterResponseDTO;
import com.juancasterba.taskflow_api.security.model.User;

public class UserMapper {

    public static User toUserEntity(RegisterRequestDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        return user;
    }

    public static RegisterResponseDTO toUserResponseDTO(User user) {
        RegisterResponseDTO dto = new RegisterResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        return dto;
    }

}
