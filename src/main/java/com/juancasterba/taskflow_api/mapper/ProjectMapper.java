package com.juancasterba.taskflow_api.mapper;

import com.juancasterba.taskflow_api.dto.CreateProjectRequestDTO;
import com.juancasterba.taskflow_api.dto.CreateTaskRequestDTO;
import com.juancasterba.taskflow_api.dto.ProjectResponseDTO;
import com.juancasterba.taskflow_api.dto.TaskResponseDTO;
import com.juancasterba.taskflow_api.model.Project;
import com.juancasterba.taskflow_api.model.Task;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public TaskResponseDTO toTaskDTO(Task task){
        return TaskResponseDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .completed(task.isCompleted())
                .build();
    }

    public ProjectResponseDTO toProjectDTO(Project project) {
        if (project == null) {
            return null;
        }
        return ProjectResponseDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .ownerUsername(project.getOwner().getUsername())
                .tasks(project.getTasks().stream().map(this::toTaskDTO).toList())
                .build();
    }

    public Project toProjectEntity(CreateProjectRequestDTO dto){
        if (dto == null){
            return null;
        }
        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        return project;
    }

    public Task toTaskEntity(CreateTaskRequestDTO dto){
        if (dto == null){
            return null;
        }
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setCompleted(false);
        return task;
    }

}
