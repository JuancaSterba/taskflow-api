package com.juancasterba.taskflow_api.service;

import com.juancasterba.taskflow_api.exception.ResourceNotFoundException;
import com.juancasterba.taskflow_api.model.Project;
import com.juancasterba.taskflow_api.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService{

    private final ProjectRepository projectRepository;

    @Override
    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @Override
    public Project getProjectById(Long id) throws ResourceNotFoundException {
        return projectRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException("No project found")
        );
    }

    @Override
    public Project updateProject(Long id, Project projectDetails) throws ResourceNotFoundException {
        Project project=projectRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("No project found")
        );
        project.setName(projectDetails.getName());
        project.setDescription(projectDetails.getDescription());
        return projectRepository.save(project);
    }

    @Override
    public void deleteProject(Long id) throws ResourceNotFoundException {
        if(!projectRepository.existsById(id)){
            throw new ResourceNotFoundException("No project found with id: "+id);
        }
        projectRepository.deleteById(id);
    }

}
