package com.juancasterba.taskflow_api.service;

import com.juancasterba.taskflow_api.dto.CreateProjectRequestDTO;
import com.juancasterba.taskflow_api.dto.ProjectResponseDTO;
import com.juancasterba.taskflow_api.exception.ResourceNotFoundException;
import com.juancasterba.taskflow_api.mapper.ProjectMapper;
import com.juancasterba.taskflow_api.model.Project;
import com.juancasterba.taskflow_api.repository.ProjectRepository;
import com.juancasterba.taskflow_api.security.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
    public Page<ProjectResponseDTO> getAllProjects(Pageable pageable) {
        Page<Project> projectPage = projectRepository.findAll(pageable);
        return projectPage.map(ProjectMapper::toProjectDTO);
    }

    @Override
    public ProjectResponseDTO getProjectById(Long id) throws ResourceNotFoundException {
        return ProjectMapper.toProjectDTO(projectRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException("No project found"))
        );
    }

    @Override
    public ProjectResponseDTO updateProject(Long id, CreateProjectRequestDTO projectDTO) throws ResourceNotFoundException {
        Project project=projectRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("No project found")
        );
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        return ProjectMapper.toProjectDTO(projectRepository.save(project));
    }

    @Override
    public void deleteProject(Long id) throws ResourceNotFoundException {
        if(!projectRepository.existsById(id)){
            throw new ResourceNotFoundException("No project found with id: "+id);
        }
        projectRepository.deleteById(id);
    }

}
