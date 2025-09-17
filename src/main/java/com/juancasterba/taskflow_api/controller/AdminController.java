package com.juancasterba.taskflow_api.controller;

import com.juancasterba.taskflow_api.dto.ErrorResponseDTO;
import com.juancasterba.taskflow_api.dto.ProjectResponseDTO;
import com.juancasterba.taskflow_api.dto.TaskResponseDTO;
import com.juancasterba.taskflow_api.service.ProjectService;
import com.juancasterba.taskflow_api.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final ProjectService projectService;
    private final TaskService taskService;

    @Operation(
            summary = "Hard delete a project by ID",
            description = "Permanently deletes a project and all its associated tasks from the database. This action is irreversible.",
            tags = "Admin: Projects Management"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Project hard deleted successfully."),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. The authentication token is invalid or has not been provided.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden. User does not have admin privileges.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found. The project with the specified ID does not exist.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @DeleteMapping("/projects/{id}/hard-delete")
    public ResponseEntity<Void> hardDeleteProject(
            @Parameter(description = "ID of the project to be permanently deleted.", required = true) @PathVariable Long id) {
        projectService.hardDeleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get all projects (Admin View)",
            description = "Returns a paginated list of ALL projects in the system, including ACTIVE and ARCHIVED ones, for all users.",
            tags = "Admin: Projects Management"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of all projects obtained successfully.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. The authentication token is invalid or has not been provided.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden. User does not have admin privileges.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @GetMapping("/projects")
    public ResponseEntity<Page<ProjectResponseDTO>> getAllProjectsIncludingArchived(Pageable pageable){
        Page<ProjectResponseDTO> projectPage = projectService.findAllProjectsForAdmin(pageable);
        return ResponseEntity.ok(projectPage);
    }
    
    @GetMapping("/tasks")
    @Operation(
            summary = "Get all tasks (Admin View)",
            description = "Returns a paginated list of ALL tasks in the system, including ACTIVE and ARCHIVED ones.",
            tags = "Admin: Tasks Management"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of all tasks obtained successfully.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. The authentication token is invalid or has not been provided.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden. User does not have admin privileges.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<Page<TaskResponseDTO>> getAllTasksForAdmin(Pageable pageable) {
        return ResponseEntity.ok(taskService.findAllTasksForAdmin(pageable));
    }

    @DeleteMapping("/tasks/{id}/hard-delete")
    @Operation(
            summary = "Hard delete a task by ID",
            description = "Permanently deletes a single task from the database. This action is irreversible.",
            tags = "Admin: Tasks Management"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task hard deleted successfully."),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. The authentication token is invalid or has not been provided.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden. User does not have admin privileges.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found. The task with the specified ID does not exist.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<Void> hardDeleteTask(
            @Parameter(description = "ID of the task to be permanently deleted.", required = true) @PathVariable Long id) {
        taskService.hardDeleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
