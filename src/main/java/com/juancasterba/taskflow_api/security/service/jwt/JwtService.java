package com.juancasterba.taskflow_api.security.service.jwt;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

    public String generateToken(UserDetails userDetails);

}
