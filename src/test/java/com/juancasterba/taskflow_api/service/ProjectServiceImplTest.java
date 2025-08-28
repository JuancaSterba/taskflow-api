package com.juancasterba.taskflow_api.service;

import com.juancasterba.taskflow_api.dto.CreateProjectRequestDTO;
import com.juancasterba.taskflow_api.dto.ProjectResponseDTO;
import com.juancasterba.taskflow_api.exception.ResourceNotFoundException;
import com.juancasterba.taskflow_api.model.Project;
import com.juancasterba.taskflow_api.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Project project;
    private CreateProjectRequestDTO createProjectRequestDTO;

    @BeforeEach
    void setup() {
        // Objeto Entidad para las respuestas del repositorio
        project = new Project();
        project.setId(1L);
        project.setName("Proyecto de Prueba");
        project.setDescription("Descripción de prueba");

        // Objeto DTO para las entradas del servicio (usado en create y update)
        createProjectRequestDTO = new CreateProjectRequestDTO();
        createProjectRequestDTO.setName("Proyecto de Prueba");
        createProjectRequestDTO.setDescription("Descripción de prueba");
    }

    @Test
    @DisplayName("Test para crear un proyecto exitosamente")
    void givenProjectRequestDTO_whenCreateProject_thenReturnProjectResponseDTO() {
        // Given
        given(projectRepository.save(any(Project.class))).willAnswer(invocation -> {
            Project p = invocation.getArgument(0);
            p.setId(1L); // Simulamos que la BD asigna un ID
            return p;
        });

        // When
        ProjectResponseDTO responseDTO = projectService.createProject(createProjectRequestDTO);

        // Then
        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getId()).isEqualTo(1L);
        assertThat(responseDTO.getName()).isEqualTo(createProjectRequestDTO.getName());

        // Verificamos que el objeto correcto fue pasado al método save
        ArgumentCaptor<Project> projectArgumentCaptor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(projectArgumentCaptor.capture());
        assertThat(projectArgumentCaptor.getValue().getName()).isEqualTo("Proyecto de Prueba");
    }

    @Test
    @DisplayName("Test para listar todos los proyectos")
    void givenProjectsList_whenGetAllProjects_thenReturnProjectResponseDTOList() {
        // Given
        Project anotherProject = new Project();
        anotherProject.setId(2L);
        anotherProject.setName("Otro Proyecto");
        anotherProject.setDescription("Otra descripción");

        given(projectRepository.findAll()).willReturn(List.of(project, anotherProject));

        // When
        List<ProjectResponseDTO> projectList = projectService.getAllProjects();

        // Then
        assertThat(projectList).isNotNull();
        assertThat(projectList.size()).isEqualTo(2);
        assertThat(projectList.get(0).getName()).isEqualTo("Proyecto de Prueba");
        assertThat(projectList.get(1).getName()).isEqualTo("Otro Proyecto");
    }

    @Test
    @DisplayName("Test para listar proyectos cuando no hay ninguno")
    void givenEmptyProjectsList_whenGetAllProjects_thenReturnEmptyList() {
        // Given
        given(projectRepository.findAll()).willReturn(Collections.emptyList());

        // When
        List<ProjectResponseDTO> projectList = projectService.getAllProjects();

        // Then
        assertThat(projectList).isNotNull();
        assertThat(projectList).isEmpty();
    }

    @Test
    @DisplayName("Test para obtener un proyecto por un ID existente")
    void givenExistingProjectId_whenGetProjectById_thenReturnProjectResponseDTO() {
        // Given
        given(projectRepository.findById(1L)).willReturn(Optional.of(project));

        // When
        ProjectResponseDTO responseDTO = projectService.getProjectById(1L);

        // Then
        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getId()).isEqualTo(1L);
        assertThat(responseDTO.getName()).isEqualTo(project.getName());
    }

    @Test
    @DisplayName("Test para obtener un proyecto por un ID que no existe")
    void givenNonExistentProjectId_whenGetProjectById_thenThrowsResourceNotFoundException() {
        // Given
        long nonExistentId = 99L;
        given(projectRepository.findById(nonExistentId)).willReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            projectService.getProjectById(nonExistentId);
        });

        verify(projectRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("Test para actualizar un proyecto exitosamente")
    void givenProjectIdAndRequestDTO_whenUpdateProject_thenReturnUpdatedProjectResponseDTO() {
        // Given
        long projectId = 1L;
        given(projectRepository.findById(projectId)).willReturn(Optional.of(project));
        given(projectRepository.save(any(Project.class))).willAnswer(invocation -> invocation.getArgument(0));

        CreateProjectRequestDTO updateRequest = new CreateProjectRequestDTO();
        updateRequest.setName("Proyecto Actualizado");
        updateRequest.setDescription("Descripción actualizada");

        // When
        ProjectResponseDTO updatedProjectDTO = projectService.updateProject(projectId, updateRequest);

        // Then
        assertThat(updatedProjectDTO).isNotNull();
        assertThat(updatedProjectDTO.getName()).isEqualTo("Proyecto Actualizado");
        assertThat(updatedProjectDTO.getDescription()).isEqualTo("Descripción actualizada");

        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(projectCaptor.capture());
        assertThat(projectCaptor.getValue().getName()).isEqualTo("Proyecto Actualizado");
    }

    @Test
    @DisplayName("Test para actualizar un proyecto que no existe")
    void givenNonExistentProjectId_whenUpdateProject_thenThrowsResourceNotFoundException() {
        // Given
        long nonExistentId = 99L;
        given(projectRepository.findById(nonExistentId)).willReturn(Optional.empty());

        CreateProjectRequestDTO updateRequest = new CreateProjectRequestDTO();
        updateRequest.setName("No importa");

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            projectService.updateProject(nonExistentId, updateRequest);
        });

        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    @DisplayName("Test para eliminar un proyecto por su ID")
    void givenExistingProjectId_whenDeleteProject_thenDeletesSuccessfully() {
        // Given
        long projectId = 1L;
        given(projectRepository.existsById(projectId)).willReturn(true);
        willDoNothing().given(projectRepository).deleteById(projectId);

        // When
        assertDoesNotThrow(() -> projectService.deleteProject(projectId));

        // Then
        verify(projectRepository, times(1)).deleteById(projectId);
    }

    @Test
    @DisplayName("Test para eliminar un proyecto con ID que no existe")
    void givenNonExistentProjectId_whenDeleteProject_thenThrowsResourceNotFoundException() {
        // Given
        long nonExistentId = 99L;
        given(projectRepository.existsById(nonExistentId)).willReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            projectService.deleteProject(nonExistentId);
        });

        verify(projectRepository, never()).deleteById(anyLong());
    }

}