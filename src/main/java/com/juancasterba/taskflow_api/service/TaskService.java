package com.juancasterba.taskflow_api.service;

import com.juancasterba.taskflow_api.dto.CreateTaskRequestDTO;
import com.juancasterba.taskflow_api.dto.TaskResponseDTO;
import com.juancasterba.taskflow_api.exception.ResourceNotFoundException;

import java.util.List;

public interface TaskService {

    TaskResponseDTO createTask(CreateTaskRequestDTO taskDTO);
    List<TaskResponseDTO> getAllTasks();
    TaskResponseDTO getTaskById (Long id) throws ResourceNotFoundException;
    TaskResponseDTO updateTask (Long id, CreateTaskRequestDTO taskDTO) throws ResourceNotFoundException;
    void deleteById (Long id) throws ResourceNotFoundException;
    TaskResponseDTO createTaskForProject (Long projectId, CreateTaskRequestDTO taskDTO);

}
