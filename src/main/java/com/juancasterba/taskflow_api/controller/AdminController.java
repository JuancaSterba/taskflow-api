package com.juancasterba.taskflow_api.controller;

import com.juancasterba.taskflow_api.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin")
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

}
