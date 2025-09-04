package com.juancasterba.taskflow_api.security.repository;

import com.juancasterba.taskflow_api.security.model.Role;
import com.juancasterba.taskflow_api.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByRole(Role role);

}
