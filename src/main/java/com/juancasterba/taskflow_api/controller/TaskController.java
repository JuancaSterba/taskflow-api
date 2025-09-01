package com.juancasterba.taskflow_api.controller;

import com.juancasterba.taskflow_api.dto.CreateTaskRequestDTO;
import com.juancasterba.taskflow_api.dto.ErrorResponseDTO;
import com.juancasterba.taskflow_api.dto.TaskResponseDTO;
import com.juancasterba.taskflow_api.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Endpoints for managing tasks.")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    @Operation(
            summary = "Get all tasks",
            description = "Allows to get a list of all tasks."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of tasks obtained successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TaskResponseDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. The authentication token is invalid or has not been provided.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public List<TaskResponseDTO> getAllTasks(){
        return taskService.getAllTasks();
    }

    @PostMapping
    @Operation(
            summary = "Create a new task",
            description = "Allows to create a new task with the provided data. By default, it is not associated with any project."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Task created successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request. Check the task data.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. The authentication token is invalid or has not been provided.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody CreateTaskRequestDTO taskDTO){
        TaskResponseDTO createdTask = taskService.createTask(taskDTO);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a task by its ID",
            description = "Allows to get detailed information of a specific task using its ID.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID of the task to get.",
                            required = true,
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "integer", format = "int64")
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Task obtained successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. The authentication token is invalid or has not been provided.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found. The task with the specified ID does not exist.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long id){
        return new ResponseEntity<>(taskService.getTaskById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a task",
            description = "Allows to update the information of an existing task using its ID.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID of the task to update.",
                            required = true,
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "integer", format = "int64")
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Task updated successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request. Check the task data.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. The authentication token is invalid or has not been provided.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found. The task with the specified ID does not exist.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable Long id, @Valid @RequestBody CreateTaskRequestDTO taskDTO){
        TaskResponseDTO updatedTask = taskService.updateTask(id, taskDTO);
        return new ResponseEntity<>(updatedTask, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a task by its ID",
            description = "Allows to permanently delete a task using its ID.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID of the task to delete.",
                            required = true,
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "integer", format = "int64")
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Task deleted successfully."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. The authentication token is invalid or has not been provided.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found. The task with the specified ID does not exist.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<?> deleteTaskById(@PathVariable Long id){
        taskService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
}
