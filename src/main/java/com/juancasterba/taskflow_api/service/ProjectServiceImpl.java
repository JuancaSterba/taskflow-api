package com.juancasterba.taskflow_api.service;

import com.juancasterba.taskflow_api.dto.CreateProjectRequestDTO;
import com.juancasterba.taskflow_api.dto.ProjectResponseDTO;
import com.juancasterba.taskflow_api.exception.ResourceNotFoundException;
import com.juancasterba.taskflow_api.mapper.ProjectMapper;
import com.juancasterba.taskflow_api.model.Project;
import com.juancasterba.taskflow_api.model.Status;
import com.juancasterba.taskflow_api.repository.ProjectRepository;
import com.juancasterba.taskflow_api.security.model.User;
import com.juancasterba.taskflow_api.security.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService{

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public ProjectResponseDTO createProject(CreateProjectRequestDTO projectDTO) {
        User currentUser = getCurrentAuthenticatedUser();
        Project project = projectMapper.toProjectEntity(projectDTO);
        project.setOwner(currentUser);
        Project savedProject = projectRepository.save(project);
        return projectMapper.toProjectDTO(savedProject);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectResponseDTO> getAllProjects(Pageable pageable) {
        User currentUser = getCurrentAuthenticatedUser();
        Page<Project> projectPage = projectRepository.findByOwner(currentUser, pageable);
        return projectPage.map(projectMapper::toProjectDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponseDTO getProjectById(Long id) {
        User currentUser = getCurrentAuthenticatedUser();
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        // Verificación de propiedad (ADMIN puede ver todo)
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        if (!isAdmin && !project.getOwner().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Project not found with id: " + id);
        }

        return projectMapper.toProjectDTO(project);
    }

    @Override
    @Transactional
    public ProjectResponseDTO updateProject(Long id, CreateProjectRequestDTO projectDTO) {
        User currentUser = getCurrentAuthenticatedUser();
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        // Verificación de propiedad
        if (!project.getOwner().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Project not found with id: " + id);
        }

        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        Project updatedProject = projectRepository.save(project);
        return projectMapper.toProjectDTO(updatedProject);
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        User currentUser = getCurrentAuthenticatedUser();
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        // Verificación de propiedad
        if (!project.getOwner().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Project not found with id: " + id);
        }

        project.setStatus(Status.ARCHIVED);
        projectRepository.save(project);
    }

    private User getCurrentAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void hardDeleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project not found with id: " + id);
        }
        projectRepository.deleteById(id);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Page<ProjectResponseDTO> findAllProjectsForAdmin(Pageable pageable) {
        Session session= entityManager.unwrap(Session.class);
        session.disableFilter("activeStatusFilter");
        Page<Project> projectPage = projectRepository.findAll(pageable);
        return projectPage.map(projectMapper::toProjectDTO);
    }

}
