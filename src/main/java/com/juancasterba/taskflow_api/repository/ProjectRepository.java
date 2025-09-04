package com.juancasterba.taskflow_api.repository;

import com.juancasterba.taskflow_api.model.Project;
import com.juancasterba.taskflow_api.security.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Page<Project> findByOwner(User owner, Pageable pageable);
}
