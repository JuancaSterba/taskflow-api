package com.juancasterba.taskflow_api.service;

import com.juancasterba.taskflow_api.dto.CreateProjectRequestDTO;
import com.juancasterba.taskflow_api.dto.ProjectResponseDTO;
import com.juancasterba.taskflow_api.exception.ResourceNotFoundException;
import com.juancasterba.taskflow_api.mapper.ProjectMapper;
import com.juancasterba.taskflow_api.model.Project;
import com.juancasterba.taskflow_api.model.Status;
import com.juancasterba.taskflow_api.repository.ProjectRepository;
import com.juancasterba.taskflow_api.security.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService{

    private final ProjectRepository projectRepository;

    @Override
    public ProjectResponseDTO createProject(CreateProjectRequestDTO projectDTO) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Project project = ProjectMapper.toProjectEntity(projectDTO);
        project.setOwner(currentUser);
        Project savedProject = projectRepository.save(project);
        return ProjectMapper.toProjectDTO(savedProject);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectResponseDTO> getAllProjects(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        // Verificamos si el usuario actual tiene el rol de ADMIN
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            // Lógica del ADMIN: buscar en todos los proyectos
            Page<Project> projectPage = projectRepository.findAll(pageable);
            return projectPage.map(ProjectMapper::toProjectDTO);
        } else {
            // Lógica del USER: buscar solo en los proyectos del dueño
            Page<Project> projectPage = projectRepository.findByOwner(currentUser, pageable);
            return projectPage.map(ProjectMapper::toProjectDTO);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponseDTO getProjectById(Long id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        // Verificación de propiedad
        if (!project.getOwner().getId().equals(currentUser.getId())) {
            // Lanzamos 404 para no revelar la existencia de recursos ajenos
            throw new ResourceNotFoundException("Project not found with id: " + id);
        }

        return ProjectMapper.toProjectDTO(project);
    }

    @Override
    public ProjectResponseDTO updateProject(Long id, CreateProjectRequestDTO projectDTO) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        // Verificación de propiedad
        if (!project.getOwner().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Project not found with id: " + id);
        }

        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        Project updatedProject = projectRepository.save(project);
        return ProjectMapper.toProjectDTO(updatedProject);
    }

    @Override
    public void deleteProject(Long id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        // Verificación de propiedad
        if (!project.getOwner().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Project not found with id: " + id);
        }

        // ¡Implementamos el borrado lógico!
        project.setStatus(Status.ARCHIVED);
        projectRepository.save(project);
    }

}
