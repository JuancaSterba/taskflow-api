package com.juancasterba.taskflow_api.service;

import com.juancasterba.taskflow_api.dto.CreateProjectRequestDTO;
import com.juancasterba.taskflow_api.dto.ProjectResponseDTO;
import com.juancasterba.taskflow_api.exception.ResourceNotFoundException;

import java.util.List;

public interface ProjectService {

    ProjectResponseDTO createProject(CreateProjectRequestDTO projectDTO);
    List<ProjectResponseDTO> getAllProjects();
    ProjectResponseDTO getProjectById(Long id) throws ResourceNotFoundException;
    ProjectResponseDTO updateProject(Long id, CreateProjectRequestDTO projectDTO) throws ResourceNotFoundException;
    void deleteProject(Long id) throws ResourceNotFoundException;

}
