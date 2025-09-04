package com.juancasterba.taskflow_api.service;

import com.juancasterba.taskflow_api.dto.CreateProjectRequestDTO;
import com.juancasterba.taskflow_api.dto.ProjectResponseDTO;
import com.juancasterba.taskflow_api.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProjectService {

    ProjectResponseDTO createProject(CreateProjectRequestDTO projectDTO);
    Page<ProjectResponseDTO> getAllProjects(Pageable pageable);
    ProjectResponseDTO getProjectById(Long id) throws ResourceNotFoundException;
    ProjectResponseDTO updateProject(Long id, CreateProjectRequestDTO projectDTO) throws ResourceNotFoundException;
    void deleteProject(Long id) throws ResourceNotFoundException;

    // ADMIN only methods
    void hardDeleteProject(Long id);

}
