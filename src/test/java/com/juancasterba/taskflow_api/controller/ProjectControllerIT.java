package com.juancasterba.taskflow_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juancasterba.taskflow_api.AbstractIntegrationTest;
import com.juancasterba.taskflow_api.dto.CreateProjectRequestDTO;
import com.juancasterba.taskflow_api.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class ProjectControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup(){
        projectRepository.deleteAll();
    }

    @Test
    @DisplayName("Test de integración para crear un proyecto")
    void givenCreateProjectRequest_whenCreateProject_thenReturnsSavedProject() throws Exception{
        // Given: Un DTO con los datos del proyecto a crear
        CreateProjectRequestDTO requestDTO=new CreateProjectRequestDTO();
        requestDTO.setName("Test Project");
        requestDTO.setDescription("This is a test project");

        // When: Realizamos una petición POST simulada al endpoint de creación
        ResultActions response = mockMvc.perform(post("/api/v1/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)));

        // Then: Verificamos la respuesta HTTP y el contenido del JSON
        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(requestDTO.getName())))
                .andExpect(jsonPath("$.description", is(requestDTO.getDescription())));

        // Y verificamos que el proyecto realmente se guardó en la base de datos
        assertEquals(1, projectRepository.findAll().size());

    }

}