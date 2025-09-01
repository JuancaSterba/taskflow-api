package com.juancasterba.taskflow_api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;

@OpenAPIDefinition(
        info=@Info(
                title = "TaskFlow API",
                description = "A RESTful API for managing Projects and Tasks.",
                version = "v1.0",
                contact = @Contact(
                        name = "Juan Carlos Sterba",
                        url = "https://github.com/JuancaSterba",
                        email = "sjcexe@gmail.com"
                )
        ),
        servers = {
                @Server(
                        description = "Local Development Server",
                        url = "http://localhost:8080"
                )
        },
        security = @SecurityRequirement(
                name = "bearerAuth"
        ),
        tags = {
                @Tag(name = "Health Check", description = "Endpoints for checking the API status."),
                @Tag(name = "Authentication", description = "Endpoints for user authentication and registration."),
                @Tag(name = "Projects", description = "Endpoints for managing projects."),
                @Tag(name = "Tasks", description = "Endpoints for managing tasks.")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT-based authentication. Enter your token below.",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER,
        paramName = HttpHeaders.AUTHORIZATION
)
public class OpenApiConfig {
}
