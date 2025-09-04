package com.juancasterba.taskflow_api.config;

import com.juancasterba.taskflow_api.security.model.Role;
import com.juancasterba.taskflow_api.security.model.User;
import com.juancasterba.taskflow_api.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByRole(Role.ADMIN).isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setEmail("admin@taskflow.com");
            adminUser.setPassword(passwordEncoder.encode("adminPass")); // Usa una contraseña segura en un proyecto real
            adminUser.setRole(Role.ADMIN);

            userRepository.save(adminUser);
            System.out.println(">>> Administrador de TaskFlow creado exitosamente!");
        }
    }
}
