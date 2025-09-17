package com.juancasterba.taskflow_api.repository;

import com.juancasterba.taskflow_api.model.Project;
import com.juancasterba.taskflow_api.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByProject(Project project, Pageable pageable);
}
