package com.juancasterba.taskflow_api.security.config;

import com.juancasterba.taskflow_api.security.model.Role;
import com.juancasterba.taskflow_api.security.model.User;
import com.juancasterba.taskflow_api.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.admin.username}")
    private String ADMIN_USERNAME;

    @Value("${spring.admin.password}")
    private String ADMIN_PASSWORD;

    @Value("${spring.admin.email}")
    private String ADMIN_EMAIL;

    @Override
    public void run(String... args) throws Exception {
        // Verificamos si ya existe un usuario con el rol de ADMIN
        if (userRepository.findByRole(Role.ADMIN).isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername(ADMIN_USERNAME);
            adminUser.setEmail(ADMIN_EMAIL);
            // En un proyecto real, esta contraseña estaría en una variable de entorno
            adminUser.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
            adminUser.setRole(Role.ADMIN);

            userRepository.save(adminUser);
            System.out.println(">>> Usuario Administrador de TaskFlow creado exitosamente!");
        }
    }
}