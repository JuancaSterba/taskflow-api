package com.juancasterba.taskflow_api.controller;

import com.juancasterba.taskflow_api.dto.*;
import com.juancasterba.taskflow_api.service.ProjectService;
import com.juancasterba.taskflow_api.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Endpoints for managing projects.")
public class ProjectController {

    private final ProjectService projectService;
    private final TaskService taskService;

    @GetMapping
    @Operation(
            summary = "Get all projects",
            description = "Allows to get a paginated list of all projects."
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
            description = "Allows to create a new project with the provided data."
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
            description = "Allows to get detailed information of a specific project using its ID.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID of the project to get.",
                            required = true,
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "integer", format = "int64")
                    )
            }
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
                    description = "Not found. The project with the specified ID does not exist.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<ProjectResponseDTO> getProjectById(@PathVariable Long id){
        return new ResponseEntity<>(projectService.getProjectById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a project",
            description = "Allows to update the information of an existing project using its ID.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID of the project to update.",
                            required = true,
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "integer", format = "int64")
                    )
            }
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
                    description = "Not found. The project with the specified ID does not exist.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<ProjectResponseDTO> updateProject(@PathVariable Long id, @Valid @RequestBody CreateProjectRequestDTO projectDTO){
        ProjectResponseDTO updatedProject = projectService.updateProject(id, projectDTO);
        return new ResponseEntity<>(updatedProject, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a project by its ID",
            description = "Allows to permanently delete a project using its ID.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID of the project to delete.",
                            required = true,
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "integer", format = "int64")
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Project deleted successfully."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. The authentication token is invalid or has not been provided.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found. The project with the specified ID does not exist.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<?> deleteProjectById(@PathVariable Long id){
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{projectId}/tasks")
    @Operation(
            summary = "Create a task for a project",
            description = "Allows to create a new task and associate it with an existing project, identified by its ID.",
            parameters = {
                    @Parameter(
                            name = "projectId",
                            description = "ID of the project to which the task will be associated.",
                            required = true,
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "integer", format = "int64")
                    )
            }
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
                    description = "Not found. The project with the specified ID does not exist.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<TaskResponseDTO> createTaskForProject(@PathVariable Long projectId, @Valid @RequestBody CreateTaskRequestDTO taskDTO){
        TaskResponseDTO createdTask = taskService.createTaskForProject(projectId, taskDTO);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

}
