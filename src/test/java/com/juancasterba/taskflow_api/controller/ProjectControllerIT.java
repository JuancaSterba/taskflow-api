package com.juancasterba.taskflow_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juancasterba.taskflow_api.AbstractIntegrationTest;
import com.juancasterba.taskflow_api.dto.CreateProjectRequestDTO;
import com.juancasterba.taskflow_api.dto.CreateTaskRequestDTO;
import com.juancasterba.taskflow_api.model.Project;
import com.juancasterba.taskflow_api.model.Status;
import com.juancasterba.taskflow_api.repository.ProjectRepository;
import com.juancasterba.taskflow_api.security.model.Role;
import com.juancasterba.taskflow_api.security.model.User;
import com.juancasterba.taskflow_api.security.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class ProjectControllerIT extends AbstractIntegrationTest {

    private static final String API_BASE_PATH = "/api/v1/projects";
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_USER_EMAIL = "test@taskflow.com";
    private static final String DEFAULT_PASSWORD = "pass";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User mainTestUser;

    @BeforeEach
    void setup() {
        projectRepository.deleteAll();
        userRepository.deleteAll();

        mainTestUser = createUser(TEST_USERNAME, TEST_USER_EMAIL, Role.USER);
    }

    @Test
    @DisplayName("POST /projects - Should create project when user is authenticated")
    @WithMockUser(TEST_USERNAME)
    void givenAuthenticatedUser_whenCreateProject_thenReturnsCreatedProject() throws Exception {
        // Given
        CreateProjectRequestDTO requestDTO = new CreateProjectRequestDTO();
        requestDTO.setName("First Test Project");
        requestDTO.setDescription("A simple description");

        // When
        ResultActions response = mockMvc.perform(post(API_BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO))
        );

        // Then
        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(requestDTO.getName())))
                .andExpect(jsonPath("$.ownerUsername", is(TEST_USERNAME)));

        List<Project> projects = projectRepository.findAll();
        assertEquals(1, projects.size());
        assertEquals(TEST_USERNAME, projects.get(0).getOwner().getUsername());
    }

    @Test
    @DisplayName("GET /projects - Should return only own projects for a regular USER")
    @WithMockUser(TEST_USERNAME)
    void givenUserWithProjects_whenGetAllProjects_thenReturnsOnlyOwnedProjects() throws Exception {
        // Given
        User anotherUser = createUser("user2", "user2@taskflow.com", Role.USER);
        createProject("User1's Project", "Desc 1", mainTestUser);
        createProject("User2's Project", "Desc 2", anotherUser);

        // When
        ResultActions response = mockMvc.perform(get(API_BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
        );

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("User1's Project")))
                .andExpect(jsonPath("$.content[0].ownerUsername", is(TEST_USERNAME)));
    }

    @Test
    @DisplayName("GET /projects - Should return all projects for an ADMIN")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void givenAdminRole_whenGetAllProjects_thenReturnsAllProjects() throws Exception {
        // Given
        createUser("admin", "admin@taskflow.com", Role.ADMIN);
        User anotherUser = createUser("user2", "user2@taskflow.com", Role.USER);

        createProject("User1's Project", "Desc 1", mainTestUser);
        createProject("User2's Project", "Desc 2", anotherUser);

        // When
        ResultActions response = mockMvc.perform(get(API_BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    @DisplayName("GET /projects/{id} - Should return project when user is owner")
    @WithMockUser(TEST_USERNAME)
    void givenUserIsOwner_whenGetProjectById_thenReturnsProject() throws Exception {
        // Given
        Project project = createProject("Owned Project", "My project", mainTestUser);

        // When
        ResultActions response = mockMvc.perform(get(API_BASE_PATH + "/{id}", project.getId())
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(project.getName())))
                .andExpect(jsonPath("$.ownerUsername", is(mainTestUser.getUsername())));
    }

    @Test
    @DisplayName("GET /projects/{id} - Should return 404 when user is not owner")
    @WithMockUser(TEST_USERNAME)
    void givenUserIsNotOwner_whenGetProjectById_thenReturnsNotFound() throws Exception {
        // Given
        User anotherUser = createUser("anotherUser", "another@test.com", Role.USER);
        Project project = createProject("Another User's Project", "A project from someone else", anotherUser);

        // When
        ResultActions response = mockMvc.perform(get(API_BASE_PATH + "/{id}", project.getId())
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /projects/{id} - Should return 404 for non-existent project")
    @WithMockUser(TEST_USERNAME)
    void givenProjectDoesNotExist_whenGetProjectById_thenReturnsNotFound() throws Exception {
        // Given
        long nonExistentId = 999L;

        // When
        ResultActions response = mockMvc.perform(get(API_BASE_PATH + "/{id}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /projects/{id} - Should return 401 when user is not authenticated")
    void givenUserIsNotAuthenticated_whenGetProjectById_thenReturnsUnauthorized() throws Exception {
        // Given
        Project project = createProject("Some Project", "A project", mainTestUser);

        // When
        ResultActions response = mockMvc.perform(get(API_BASE_PATH + "/{id}", project.getId())
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /projects/{id} - Should update project when user is owner")
    @WithMockUser(TEST_USERNAME)
    void givenUserIsOwner_whenUpdateProject_thenReturnsUpdatedProject() throws Exception {
        // Given
        Project project = createProject("Original Name", "Original Desc", mainTestUser);
        CreateProjectRequestDTO updateRequest = new CreateProjectRequestDTO();
        updateRequest.setName("Updated Name");
        updateRequest.setDescription("Updated Desc");

        // When
        ResultActions response = mockMvc.perform(put(API_BASE_PATH + "/{id}", project.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Name")))
                .andExpect(jsonPath("$.description", is("Updated Desc")));

        Project updatedProject = projectRepository.findById(project.getId()).get();
        assertEquals("Updated Name", updatedProject.getName());
    }

    @Test
    @DisplayName("PUT /projects/{id} - Should return 404 when user is not owner")
    @WithMockUser(TEST_USERNAME)
    void givenUserIsNotOwner_whenUpdateProject_thenReturnsNotFound() throws Exception {
        // Given
        User anotherUser = createUser("anotherUser", "another@test.com", Role.USER);
        Project project = createProject("Another User's Project", "A project from someone else", anotherUser);
        CreateProjectRequestDTO updateRequest = new CreateProjectRequestDTO();
        updateRequest.setName("Updated Name");

        // When
        ResultActions response = mockMvc.perform(put(API_BASE_PATH + "/{id}", project.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)));

        // Then
        response.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /projects/{id} - Should return 400 for invalid data")
    @WithMockUser(TEST_USERNAME)
    void givenInvalidData_whenUpdateProject_thenReturnsBadRequest() throws Exception {
        // Given
        Project project = createProject("Test Project", "Test Desc", mainTestUser);
        CreateProjectRequestDTO updateRequest = new CreateProjectRequestDTO();
        updateRequest.setName(""); // Invalid name

        // When
        ResultActions response = mockMvc.perform(put(API_BASE_PATH + "/{id}", project.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)));

        // Then
        response.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /projects/{id} - Should return 404 for non-existent project")
    @WithMockUser(TEST_USERNAME)
    void givenProjectDoesNotExist_whenUpdateProject_thenReturnsNotFound() throws Exception {
        // Given
        long nonExistentId = 999L;
        CreateProjectRequestDTO updateRequest = new CreateProjectRequestDTO();
        updateRequest.setName("Doesn't matter");

        // When
        ResultActions response = mockMvc.perform(put(API_BASE_PATH + "/{id}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)));

        // Then
        response.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /projects/{id} - Should return 401 when user is not authenticated")
    void givenUserIsNotAuthenticated_whenUpdateProject_thenReturnsUnauthorized() throws Exception {
        // Given
        Project project = createProject("Some Project", "A project", mainTestUser);
        CreateProjectRequestDTO updateRequest = new CreateProjectRequestDTO();
        updateRequest.setName("Updated Name");

        // When
        ResultActions response = mockMvc.perform(put(API_BASE_PATH + "/{id}", project.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)));

        // Then
        response.andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /projects/{id} - Should archive project when user is owner")
    @WithMockUser(TEST_USERNAME)
    void givenUserIsOwner_whenDeleteProject_thenArchivesProject() throws Exception {
        // Given
        Project project = createProject("Test Project", "Test Desc", mainTestUser);
        long projectId = project.getId();

        // When
        ResultActions response = mockMvc.perform(delete(API_BASE_PATH + "/{id}", projectId));

        // Then
        response.andExpect(status().isNoContent());

        Project archivedProject = projectRepository.findById(projectId).get();
        assertEquals(Status.ARCHIVED, archivedProject.getStatus());
    }

    @Test
    @DisplayName("DELETE /projects/{id} - Should return 404 when user is not owner")
    @WithMockUser(TEST_USERNAME)
    void givenUserIsNotOwner_whenDeleteProject_thenReturnsNotFound() throws Exception {
        // Given
        User anotherUser = createUser("anotherUser", "another@test.com", Role.USER);
        Project project = createProject("Another User's Project", "A project from someone else", anotherUser);

        // When
        ResultActions response = mockMvc.perform(delete(API_BASE_PATH + "/{id}", project.getId()));

        // Then
        response.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /projects/{id} - Should return 404 for non-existent project")
    @WithMockUser(TEST_USERNAME)
    void givenProjectDoesNotExist_whenDeleteProject_thenReturnsNotFound() throws Exception {
        // Given
        long nonExistentId = 999L;

        // When
        ResultActions response = mockMvc.perform(delete(API_BASE_PATH + "/{id}", nonExistentId));

        // Then
        response.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /projects/{id} - Should return 401 when user is not authenticated")
    void givenUserIsNotAuthenticated_whenDeleteProject_thenReturnsUnauthorized() throws Exception {
        // Given
        Project project = createProject("Some Project", "A project", mainTestUser);

        // When
        ResultActions response = mockMvc.perform(delete(API_BASE_PATH + "/{id}", project.getId()));

        // Then
        response.andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /{projectId}/tasks - Should create task when user is owner")
    @WithMockUser(TEST_USERNAME)
    void givenUserIsOwner_whenCreateTaskForProject_thenReturnsCreatedTask() throws Exception {
        // Given
        Project project = createProject("Test Project", "Test Desc", mainTestUser);
        CreateTaskRequestDTO taskRequest = new CreateTaskRequestDTO();
        taskRequest.setTitle("New Task");
        taskRequest.setDescription("A new task for the project");

        // When
        ResultActions response = mockMvc.perform(post(API_BASE_PATH + "/{projectId}/tasks", project.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)));

        // Then
        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("New Task")));
    }

    @Test
    @DisplayName("POST /{projectId}/tasks - Should return 404 when user is not owner")
    @WithMockUser(TEST_USERNAME)
    void givenUserIsNotOwner_whenCreateTaskForProject_thenReturnsNotFound() throws Exception {
        // Given
        User anotherUser = createUser("anotherUser", "another@test.com", Role.USER);
        Project project = createProject("Another User's Project", "A project from someone else", anotherUser);
        CreateTaskRequestDTO taskRequest = new CreateTaskRequestDTO();
        taskRequest.setTitle("New Task");

        // When
        ResultActions response = mockMvc.perform(post(API_BASE_PATH + "/{projectId}/tasks", project.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)));

        // Then
        response.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /{projectId}/tasks - Should return 400 for invalid data")
    @WithMockUser(TEST_USERNAME)
    void givenInvalidData_whenCreateTask_thenReturnsBadRequest() throws Exception {
        // Given
        Project project = createProject("Test Project", "Test Desc", mainTestUser);
        CreateTaskRequestDTO taskRequest = new CreateTaskRequestDTO();
        taskRequest.setTitle(""); // Invalid title

        // When
        ResultActions response = mockMvc.perform(post(API_BASE_PATH + "/{projectId}/tasks", project.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)));

        // Then
        response.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /{projectId}/tasks - Should return 404 for non-existent project")
    @WithMockUser(TEST_USERNAME)
    void givenProjectDoesNotExist_whenCreateTask_thenReturnsNotFound() throws Exception {
        // Given
        long nonExistentId = 999L;
        CreateTaskRequestDTO taskRequest = new CreateTaskRequestDTO();
        taskRequest.setTitle("Doesn't matter");

        // When
        ResultActions response = mockMvc.perform(post(API_BASE_PATH + "/{projectId}/tasks", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)));

        // Then
        response.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /{projectId}/tasks - Should return 401 when user is not authenticated")
    void givenUserIsNotAuthenticated_whenCreateTask_thenReturnsUnauthorized() throws Exception {
        // Given
        Project project = createProject("Some Project", "A project", mainTestUser);
        CreateTaskRequestDTO taskRequest = new CreateTaskRequestDTO();
        taskRequest.setTitle("New Task");

        // When
        ResultActions response = mockMvc.perform(post(API_BASE_PATH + "/{projectId}/tasks", project.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)));

        // Then
        response.andExpect(status().isUnauthorized());
    }

    // --- Helper Methods ---

    private User createUser(String username, String email, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        user.setRole(role);
        return userRepository.saveAndFlush(user);
    }

    private Project createProject(String name, String description, User owner) {
        Project project = new Project(null, name, description, List.of(), owner, Status.ACTIVE);
        return projectRepository.saveAndFlush(project);
    }
}
