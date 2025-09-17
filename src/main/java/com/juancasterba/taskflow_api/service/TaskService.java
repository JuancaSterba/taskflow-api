package com.juancasterba.taskflow_api.service;

import com.juancasterba.taskflow_api.dto.CreateTaskRequestDTO;
import com.juancasterba.taskflow_api.dto.TaskResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {

    // Creates a task within a specific project
    TaskResponseDTO createTaskForProject(Long projectId, CreateTaskRequestDTO taskDTO);

    // Gets a paginated list of tasks for a specific project
    Page<TaskResponseDTO> getTasksByProjectId(Long projectId, Pageable pageable);

    // Gets a specific task by its ID
    TaskResponseDTO getTaskById(Long id);

    // Updates a specific task
    TaskResponseDTO updateTask(Long id, CreateTaskRequestDTO taskDTO);

    // Performs a soft delete (archives) a task
    void archiveTask(Long id);

    // --- ADMIN only method ---
    // Performs a hard delete of a task
    void hardDeleteTask(Long id);

    // Returns ALL tasks (active and archived) for the admin view
    Page<TaskResponseDTO> findAllTasksForAdmin(Pageable pageable);

}
