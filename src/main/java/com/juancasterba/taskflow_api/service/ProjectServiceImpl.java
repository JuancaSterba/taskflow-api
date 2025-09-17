package com.juancasterba.taskflow_api.service;

import com.juancasterba.taskflow_api.config.SecurityUtils;
import com.juancasterba.taskflow_api.dto.CreateProjectRequestDTO;
import com.juancasterba.taskflow_api.dto.ProjectResponseDTO;
import com.juancasterba.taskflow_api.exception.ResourceNotFoundException;
import com.juancasterba.taskflow_api.mapper.ProjectMapper;
import com.juancasterba.taskflow_api.model.Project;
import com.juancasterba.taskflow_api.model.Status;
import com.juancasterba.taskflow_api.repository.ProjectRepository;
import com.juancasterba.taskflow_api.security.model.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link ProjectService} interface.
 * This class contains the business logic for managing projects.
 *
 * <p>Authorization rules are enforced using a combination of Spring Security's method security
 * (e.g., {@code @PreAuthorize}) and programmatic checks within the methods to ensure
 * that users can only access or modify resources they own, unless they are an ADMIN.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService{

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final EntityManager entityManager;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public ProjectResponseDTO createProject(CreateProjectRequestDTO projectDTO) {
        User currentUser = securityUtils.getCurrentAuthenticatedUser();
        Project project = projectMapper.toProjectEntity(projectDTO);
        project.setOwner(currentUser);
        Project savedProject = projectRepository.save(project);
        return projectMapper.toProjectDTO(savedProject);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectResponseDTO> getAllProjects(Pageable pageable) {
        User currentUser = securityUtils.getCurrentAuthenticatedUser();
        Page<Project> projectPage = projectRepository.findByOwner(currentUser, pageable);
        return projectPage.map(projectMapper::toProjectDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponseDTO getProjectById(Long id) {
        User currentUser = securityUtils.getCurrentAuthenticatedUser();
        Project project = findProjectByIdOrThrow(id);

        // A user can see a project if they are the owner or an admin.
        securityUtils.checkOwnershipOrAdmin(project, currentUser);

        return projectMapper.toProjectDTO(project);
    }

    @Override
    @Transactional
    public ProjectResponseDTO updateProject(Long id, CreateProjectRequestDTO projectDTO) {
        User currentUser = securityUtils.getCurrentAuthenticatedUser();
        Project project = findProjectByIdOrThrow(id);

        // A user can update a project only if they are the owner or an admin.
        securityUtils.checkOwnershipOrAdmin(project, currentUser);

        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        Project updatedProject = projectRepository.save(project);
        return projectMapper.toProjectDTO(updatedProject);
    }

    @Override
    @Transactional
    public void archiveProject(Long id) {
        User currentUser = securityUtils.getCurrentAuthenticatedUser();
        Project project = findProjectByIdOrThrow(id);

        // A user can archive a project only if they are the owner or an admin.
        securityUtils.checkOwnershipOrAdmin(project, currentUser);

        project.setStatus(Status.ARCHIVED);
        projectRepository.save(project);
    }

    // This method is not part of the public API but is required for admin operations.
    // The @PreAuthorize annotation ensures only admins can execute it.
    // Javadoc here clarifies its purpose and constraints.
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void hardDeleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project not found with id: " + id);
        }
        projectRepository.deleteById(id);
    }

    // This method is not part of the public API but is required for admin operations.
    // The @PreAuthorize annotation ensures only admins can execute it.
    // Javadoc here clarifies its purpose and constraints.
    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Page<ProjectResponseDTO> findAllProjectsForAdmin(Pageable pageable) {
        // The "activeStatusFilter" is likely a Hibernate filter to exclude ARCHIVED items.
        // For this admin-specific method, we temporarily disable it to show all projects,
        // including archived ones.
        Session session= entityManager.unwrap(Session.class);
        session.disableFilter("activeStatusFilter");
        Page<Project> projectPage = projectRepository.findAll(pageable);
        return projectPage.map(projectMapper::toProjectDTO);
    }

    /**
     * Fetches a project by its ID from the repository.
     *
     * @param id The ID of the project to find.
     * @return The found {@link Project} entity.
     * @throws ResourceNotFoundException if no project is found with the given ID.
     */
    private Project findProjectByIdOrThrow(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
    }
}
