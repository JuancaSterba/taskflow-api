package com.juancasterba.taskflow_api.service;

import com.juancasterba.taskflow_api.model.Task;
import com.juancasterba.taskflow_api.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("This is a test task");
        task.setCompleted(false);
    }

    @Test
    @DisplayName("Test para guardar una tarea")
    void givenTaskObject_whenSaveTask_thenReturnSavedTask() {
        // Given: Preparamos el comportamiento del mock.
        // Cuando se llame a taskRepository.save() con cualquier objeto Task...
        given(taskRepository.save(any(Task.class))).willReturn(task);

        // When: Ejecutamos el método que queremos probar.
        Task savedTask = taskService.createTask(new Task());

        // Then: Verificamos los resultados.
        assertNotNull(savedTask); // Aseguramos que el resultado no es nulo
        assertEquals("Test Task", savedTask.getTitle()); // Comparamos el título
    }

    @Test
    @DisplayName("Test para obtener una tarea por su ID")
    void givenTaskId_whenGetTaskById_thenReturnTaskObject() {
        // Given: Cuando se llame a taskRepository.findById() con el ID 1...
        given(taskRepository.findById(1L)).willReturn(Optional.of(task));

        // When: Ejecutamos el método a probar.
        Task foundTask = taskService.getTaskById(1L);

        // Then: Verificamos.
        assertNotNull(foundTask);
        assertEquals(1L, foundTask.getId());
    }

    @Test
    @DisplayName("Test para obtener una tarea por un ID que no existe")
    void givenNonExistentTaskId_whenGetTaskById_thenThrowsException() {
        // Given: Cuando se llame a taskRepository.findById() con un ID que no existe...
        given(taskRepository.findById(99L)).willReturn(Optional.empty()); // Devolvemos un Optional vacío

        // When & Then: Verificamos que se lanza la excepción esperada.
        assertThrows(RuntimeException.class, () -> {
            taskService.getTaskById(99L);
        });

        // Opcional: Verificar que el método delete nunca fue llamado.
        verify(taskRepository, never()).delete(any(Task.class));
    }

}