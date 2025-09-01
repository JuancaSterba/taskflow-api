package com.juancasterba.taskflow_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Health Check", description = "Endpoints for checking the API status.")
public class HealthController {

    @GetMapping("/ping")
    @Operation(summary = "Check API status", description = "Returns 'pong' if the API is running. This endpoint is not secured.")
    @ApiResponse(responseCode = "200", description = "API is running")
    @SecurityRequirements({}) // This is key: It overrides the global security requirement for this endpoint
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
}
