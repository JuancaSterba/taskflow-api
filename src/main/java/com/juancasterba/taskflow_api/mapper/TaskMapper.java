package com.juancasterba.taskflow_api.mapper;

import com.juancasterba.taskflow_api.dto.CreateTaskRequestDTO;
import com.juancasterba.taskflow_api.dto.TaskResponseDTO;
import com.juancasterba.taskflow_api.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskResponseDTO toTaskDTO(Task task) {
        if (task == null) {
            return null;
        }
        return TaskResponseDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .completed(task.isCompleted())
                .status(task.getStatus().name())
                .build();
    }

    public Task toTaskEntity(CreateTaskRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setCompleted(dto.isCompleted()); // Asumo que el DTO tiene este campo
        return task;
    }
}