package com.juancasterba.taskflow_api.mapper;

import com.juancasterba.taskflow_api.dto.CreateProjectRequestDTO;
import com.juancasterba.taskflow_api.dto.ProjectResponseDTO;
import com.juancasterba.taskflow_api.dto.TaskResponseDTO;
import com.juancasterba.taskflow_api.model.Project;
import com.juancasterba.taskflow_api.model.Status;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProjectMapper {

    private final TaskMapper taskMapper;
    private final EntityManager entityManager;

    public ProjectResponseDTO toProjectDTO(Project project) {
        if (project == null) {
            return null;
        }
        ProjectResponseDTO.ProjectResponseDTOBuilder builder = ProjectResponseDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .status(project.getStatus().name())
                .ownerUsername(project.getOwner().getUsername());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication != null && authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        List<TaskResponseDTO> taskDTOs;
        if (isAdmin) {
            taskDTOs = project.getTasks().stream()
                    .map(taskMapper::toTaskDTO)
                    .toList();
        } else {
            taskDTOs = project.getTasks().stream()
                    .filter(task -> task.getStatus() == Status.ACTIVE) // <-- ¡LA LÓGICA EXPLÍCITA!
                    .map(taskMapper::toTaskDTO)
                    .toList();
        }

        builder.tasks(taskDTOs);

        return builder.build();
    }

    public Project toProjectEntity(CreateProjectRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        return project;
    }
}
