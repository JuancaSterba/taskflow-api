package com.juancasterba.taskflow_api.controller;

import com.juancasterba.taskflow_api.dto.ProjectResponseDTO;
import com.juancasterba.taskflow_api.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@Tag(name = "Admin: Projects Management")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final ProjectService projectService;

    @Operation(summary = "Hard delete a project by ID",
               responses = {
                   @ApiResponse(responseCode = "204", description = "Project hard deleted successfully"),
                   @ApiResponse(responseCode = "404", description = "Project not found"),
                   @ApiResponse(responseCode = "403", description = "Forbidden - User does not have admin privileges")
               })
    @DeleteMapping("/projects/{id}/hard-delete")
    public ResponseEntity<Void> hardDeleteProject(@Parameter(description = "ID of the project to hard delete", required = true) @PathVariable Long id) {
        projectService.hardDeleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get all projects (Admin View)",
            description = "Returns a paginated list of ALL projects, including ACTIVE and ARCHIVED, for all users."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all projects obtained successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have admin privileges")
    })
    @GetMapping("/projects")
    public ResponseEntity<Page<ProjectResponseDTO>> getAllProjectsIncludingArchived(Pageable pageable){
        Page<ProjectResponseDTO> projectPage = projectService.findAllProjectsForAdmin(pageable);
        return ResponseEntity.ok(projectPage);
    }
}
