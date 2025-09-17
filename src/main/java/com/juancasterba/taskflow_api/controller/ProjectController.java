package com.juancasterba.taskflow_api.controller;

import com.juancasterba.taskflow_api.dto.*;
import com.juancasterba.taskflow_api.service.ProjectService;
import com.juancasterba.taskflow_api.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Endpoints for managing projects.")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {

    private final ProjectService projectService;
    private final TaskService taskService;

    @GetMapping
    @Operation(
            summary = "Get all projects for the current user (or all if ADMIN)",
            description = "Retrieves a paginated list of projects. If the user is an ADMIN, it returns all projects. Otherwise, it returns only the projects owned by the current user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of projects obtained successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. The authentication token is invalid or has not been provided.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<Page<ProjectResponseDTO>> getAllProjects(Pageable pageable){
        Page<ProjectResponseDTO> projectPage = projectService.getAllProjects(pageable);
        return new ResponseEntity<>(projectPage, HttpStatus.OK);
    }

    @PostMapping
    @Operation(
            summary = "Create a new project",
            description = "Allows to create a new project with the provided data. The project will be owned by the current authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Project created successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProjectResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request. Check the project data.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. The authentication token is invalid or has not been provided.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<ProjectResponseDTO> createProject(@Valid @RequestBody CreateProjectRequestDTO projectDTO){
        ProjectResponseDTO createdProject = projectService.createProject(projectDTO);
        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a project by its ID",
            description = "Allows to get detailed information of a specific project using its ID. Access is restricted to the project owner."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Project obtained successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProjectResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. The authentication token is invalid or has not been provided.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found. The project with the specified ID does not exist or you do not have permission to view it.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<ProjectResponseDTO> getProjectById(@PathVariable Long id){
        return new ResponseEntity<>(projectService.getProjectById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a project",
            description = "Allows to update the information of an existing project using its ID. Access is restricted to the project owner."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Project updated successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProjectResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request. Check the project data.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. The authentication token is invalid or has not been provided.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found. The project with the specified ID does not exist or you do not have permission to modify it.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<ProjectResponseDTO> updateProject(@PathVariable Long id, @Valid @RequestBody CreateProjectRequestDTO projectDTO){
        ProjectResponseDTO updatedProject = projectService.updateProject(id, projectDTO);
        return new ResponseEntity<>(updatedProject, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Archive a project by its ID (Soft Delete)",
            description = "Allows to archive a project by setting its status to 'ARCHIVED'. This is a soft delete. Access is restricted to the project owner."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Project archived successfully."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. The authentication token is invalid or has not been provided.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found. The project with the specified ID does not exist or you do not have permission to archive it.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<?> deleteProjectById(@PathVariable Long id){
        projectService.archiveProject(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{projectId}/tasks")
    @Operation(
            summary = "Create a task for a project",
            description = "Allows to create a new task and associate it with an existing project, identified by its ID. Access is restricted to the project owner."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Task created and associated with the project successfully.",
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
                    description = "Not found. The project with the specified ID does not exist or you do not have permission to add tasks to it.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<TaskResponseDTO> createTaskForProject(@PathVariable Long projectId, @Valid @RequestBody CreateTaskRequestDTO taskDTO){
        TaskResponseDTO createdTask = taskService.createTaskForProject(projectId, taskDTO);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @GetMapping("/{projectId}/tasks")
    @Operation(
            summary = "Get all tasks for a specific project",
            description = "Retrieves a paginated list of tasks associated with a specific project ID. Access is restricted to the project owner or an admin."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of tasks obtained successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. The authentication token is invalid or has not been provided.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found. The project with the specified ID does not exist or you do not have permission to view it.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<Page<TaskResponseDTO>> getTasksByProjectId(
            @PathVariable Long projectId,
            Pageable pageable) {

        Page<TaskResponseDTO> tasksPage = taskService.getTasksByProjectId(projectId, pageable);
        return ResponseEntity.ok(tasksPage);
    }
}
