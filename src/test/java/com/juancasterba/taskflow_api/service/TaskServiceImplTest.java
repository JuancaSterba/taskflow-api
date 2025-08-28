package com.juancasterba.taskflow_api.service;

import com.juancasterba.taskflow_api.dto.CreateTaskRequestDTO;
import com.juancasterba.taskflow_api.dto.TaskResponseDTO;
import com.juancasterba.taskflow_api.exception.ResourceNotFoundException;
import com.juancasterba.taskflow_api.model.Project;
import com.juancasterba.taskflow_api.model.Task;
import com.juancasterba.taskflow_api.repository.ProjectRepository;
import com.juancasterba.taskflow_api.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task task;
    private Project project;
    private CreateTaskRequestDTO createTaskRequestDTO;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setDescription("This is a test project");

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("This is a test task");
        task.setCompleted(false);

        createTaskRequestDTO = new CreateTaskRequestDTO();
        createTaskRequestDTO.setTitle("Test Task");
        createTaskRequestDTO.setDescription("This is a test task");
        createTaskRequestDTO.setCompleted(false);
    }

    @DisplayName("Test para guardar una tarea")
    @Test
    void givenTaskRequestDTO_whenCreateTask_thenReturnTaskResponseDTO() {
        // Given
        given(taskRepository.save(any(Task.class))).willReturn(task);

        // When
        TaskResponseDTO savedTaskDTO = taskService.createTask(createTaskRequestDTO);

        // Then
        assertThat(savedTaskDTO).isNotNull();
        assertThat(savedTaskDTO.getId()).isEqualTo(1L);
        assertThat(savedTaskDTO.getTitle()).isEqualTo(createTaskRequestDTO.getTitle());
    }

    @DisplayName("Test para listar todas las tareas")
    @Test
    void givenTasksList_whenGetAllTasks_thenReturnTaskResponseDTOList() {
        // Given
        Task anotherTask = new Task();
        anotherTask.setId(2L);
        anotherTask.setTitle("Another Task");
        anotherTask.setDescription("Another description");
        anotherTask.setCompleted(true);

        given(taskRepository.findAll()).willReturn(List.of(task, anotherTask));

        // When
        List<TaskResponseDTO> taskList = taskService.getAllTasks();

        // Then
        assertThat(taskList).isNotNull();
        assertThat(taskList.size()).isEqualTo(2);
    }

    @DisplayName("Test para obtener una tarea por su ID")
    @Test
    void givenTaskId_whenGetTaskById_thenReturnTaskResponseDTO() {
        // Given
        given(taskRepository.findById(1L)).willReturn(Optional.of(task));

        // When
        TaskResponseDTO foundTaskDTO = taskService.getTaskById(1L);

        // Then
        assertThat(foundTaskDTO).isNotNull();
        assertThat(foundTaskDTO.getId()).isEqualTo(task.getId());
        assertThat(foundTaskDTO.getTitle()).isEqualTo(task.getTitle());
    }

    @DisplayName("Test para obtener una tarea por un ID que no existe")
    @Test
    void givenNonExistentTaskId_whenGetTaskById_thenThrowsResourceNotFoundException() {
        // Given
        long nonExistentId = 99L;
        given(taskRepository.findById(nonExistentId)).willReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.getTaskById(nonExistentId);
        });
        verify(taskRepository, times(1)).findById(nonExistentId);
    }

    @DisplayName("Test para actualizar una tarea")
    @Test
    void givenTaskIdAndTaskRequestDTO_whenUpdateTask_thenReturnUpdatedTaskResponseDTO() {
        // Given
        given(taskRepository.findById(1L)).willReturn(Optional.of(task));
        given(taskRepository.save(any(Task.class))).willAnswer(invocation -> invocation.getArgument(0));

        CreateTaskRequestDTO updateRequest = new CreateTaskRequestDTO();
        updateRequest.setTitle("Updated Title");
        updateRequest.setDescription("Updated Description");
        updateRequest.setCompleted(true);

        // When
        TaskResponseDTO updatedTaskDTO = taskService.updateTask(1L, updateRequest);

        // Then
        assertThat(updatedTaskDTO).isNotNull();
        assertThat(updatedTaskDTO.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedTaskDTO.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedTaskDTO.isCompleted()).isTrue();

        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskArgumentCaptor.capture());
        assertThat(taskArgumentCaptor.getValue().getTitle()).isEqualTo("Updated Title");
    }

    @DisplayName("Test para actualizar una tarea que no existe")
    @Test
    void givenNonExistentTaskId_whenUpdateTask_thenThrowsResourceNotFoundException() {
        // Given
        long nonExistentId = 99L;
        given(taskRepository.findById(nonExistentId)).willReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.updateTask(nonExistentId, createTaskRequestDTO);
        });

        verify(taskRepository, never()).save(any(Task.class));
    }

    @DisplayName("Test para eliminar una tarea por su ID")
    @Test
    void givenTaskId_whenDeleteById_thenDeletesTask() {
        // Given
        long taskId = 1L;
        given(taskRepository.existsById(taskId)).willReturn(true);
        willDoNothing().given(taskRepository).deleteById(taskId);

        // When
        assertDoesNotThrow(() -> taskService.deleteById(taskId));

        // Then
        verify(taskRepository, times(1)).deleteById(taskId);
    }

    @DisplayName("Test para eliminar una tarea con ID que no existe")
    @Test
    void givenNonExistentTaskId_whenDeleteById_thenThrowsResourceNotFoundException() {
        // Given
        long nonExistentId = 99L;
        given(taskRepository.existsById(nonExistentId)).willReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.deleteById(nonExistentId);
        });

        verify(taskRepository, never()).deleteById(anyLong());
    }

    @DisplayName("Test para crear una tarea para un proyecto existente")
    @Test
    void givenProjectIdAndTaskRequest_whenCreateTaskForProject_thenReturnTaskResponseDTO() {
        // Given
        long projectId = 1L;
        task.setProject(project); // The saved task will have the project
        given(projectRepository.findById(projectId)).willReturn(Optional.of(project));
        given(taskRepository.save(any(Task.class))).willReturn(task);

        // When
        TaskResponseDTO savedTaskDTO = taskService.createTaskForProject(projectId, createTaskRequestDTO);

        // Then
        assertThat(savedTaskDTO).isNotNull();
        assertThat(savedTaskDTO.getId()).isEqualTo(1L);

        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskArgumentCaptor.capture());
        Task savedTask = taskArgumentCaptor.getValue();

        assertThat(savedTask.getProject()).isNotNull();
        assertThat(savedTask.getProject().getId()).isEqualTo(projectId);
    }

    @DisplayName("Test para crear una tarea para un proyecto que no existe")
    @Test
    void givenNonExistentProjectId_whenCreateTaskForProject_thenThrowsResourceNotFoundException() {
        // Given
        long nonExistentProjectId = 99L;
        given(projectRepository.findById(nonExistentProjectId)).willReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.createTaskForProject(nonExistentProjectId, createTaskRequestDTO);
        });

        verify(taskRepository, never()).save(any(Task.class));
    }

}