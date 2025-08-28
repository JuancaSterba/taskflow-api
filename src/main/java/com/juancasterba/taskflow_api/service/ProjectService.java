package com.juancasterba.taskflow_api.service;

import com.juancasterba.taskflow_api.exception.ResourceNotFoundException;
import com.juancasterba.taskflow_api.model.Project;

import java.util.List;

public interface ProjectService {

    Project createProject(Project project);
    List<Project> getAllProjects();
    Project getProjectById(Long id) throws ResourceNotFoundException;
    Project updateProject(Long id, Project projectDetails) throws ResourceNotFoundException;
    void deleteProject(Long id) throws ResourceNotFoundException;

}
