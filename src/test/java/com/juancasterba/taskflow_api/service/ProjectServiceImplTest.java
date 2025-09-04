package com.juancasterba.taskflow_api.service;

import com.juancasterba.taskflow_api.dto.CreateProjectRequestDTO;
import com.juancasterba.taskflow_api.dto.ProjectResponseDTO;
import com.juancasterba.taskflow_api.exception.ResourceNotFoundException;
import com.juancasterba.taskflow_api.mapper.ProjectMapper;
import com.juancasterba.taskflow_api.model.Project;
import com.juancasterba.taskflow_api.model.Status;
import com.juancasterba.taskflow_api.repository.ProjectRepository;
import com.juancasterba.taskflow_api.security.model.User;
import com.juancasterba.taskflow_api.security.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private String username = "testuser";
    private User currentUser;
    private SecurityContext securityContext;
    private Authentication authentication;
    private Pageable pageable;
    private User anotherUser;

    @BeforeEach
    void setUp() {// Reset SecurityContextHolder before each test
        SecurityContextHolder.clearContext();

        // Ahora asignamos a los CAMPOS de la clase, no a variables locales
        authentication = mock(Authentication.class);
        securityContext = mock(SecurityContext.class);

        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(username);
        SecurityContextHolder.setContext(securityContext);

        // Initialize currentUser
        currentUser = new User();
        currentUser.setUsername(username);
        currentUser.setId(1L);

        // Initialize pageable
        pageable = PageRequest.of(0, 10);

        // Initialize anotherUser
        anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("anotheruser");

        // Mock UserRepository
        lenient().when(userRepository.findByUsername(username)).thenReturn(Optional.of(currentUser));
    }

    private Project createProjectEntity(Long id, String name, String description, User owner) {
        Project project = new Project();
        project.setId(id);
        project.setName(name);
        project.setDescription(description);
        project.setOwner(owner);
        project.setTasks(new ArrayList<>());
        return project;
    }

    @Test
    @DisplayName("Test para crear un proyecto exitosamente")
    void createProject() {
        // === Given (Preparación) ===

        // 1. Preparamos el DTO de entrada
        CreateProjectRequestDTO requestDTO = new CreateProjectRequestDTO("Test Project", "Description for test project");

        // 2. Creamos la entidad que el mapper y el repo usarán
        Project projectToSave = new Project();
        projectToSave.setName(requestDTO.getName());
        projectToSave.setDescription(requestDTO.getDescription());

        Project savedProject = new Project();
        savedProject.setId(1L);
        savedProject.setName(requestDTO.getName());
        savedProject.setDescription(requestDTO.getDescription());
        savedProject.setOwner(currentUser);

        // 3. ¡Enseñamos a nuestros mocks a comportarse!
        // Cuando el mapper convierta el DTO a entidad...
        when(projectMapper.toProjectEntity(requestDTO)).thenReturn(projectToSave);
        // Cuando el repositorio guarde la entidad...
        when(projectRepository.save(projectToSave)).thenReturn(savedProject);
        // Cuando el mapper convierta la entidad guardada de vuelta a un DTO de respuesta...
        when(projectMapper.toProjectDTO(savedProject)).thenReturn(
                new ProjectResponseDTO(1L, "Test Project", "Description for test project", username, Collections.emptyList())
        );


        // === When (Ejecución) ===
        ProjectResponseDTO result = projectService.createProject(requestDTO);


        // === Then (Verificación) ===
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Project", result.getName());
        assertEquals(username, result.getOwnerUsername());

        // Verificamos que el método save del repositorio fue llamado exactamente una vez
        verify(projectRepository, times(1)).save(projectToSave);
    }

    @Test
    void getAllProjects_asRegularUser() {
        // === Given (Preparación) ===

        // 1. Preparamos la respuesta que esperamos del Repositorio
        Project project1 = createProjectEntity(1L, "User Project 1", "Description 1", currentUser);
        Project project2 = createProjectEntity(2L, "User Project 2", "Description 2", currentUser);
        List<Project> userProjects = Arrays.asList(project1, project2);
        Page<Project> userProjectsPage = new PageImpl<>(userProjects, pageable, userProjects.size());

        // Le decimos al repositorio mock qué devolver
        when(projectRepository.findByOwner(currentUser, pageable)).thenReturn(userProjectsPage);

        // 2. Preparamos los DTOs que esperamos que el Mapper genere
        ProjectResponseDTO dto1 = new ProjectResponseDTO(1L, "User Project 1", "Description 1", "testuser", List.of());
        ProjectResponseDTO dto2 = new ProjectResponseDTO(2L, "User Project 2", "Description 2", "testuser", List.of());

        // 3. ¡Le enseñamos a nuestro Mapper mock cómo convertir cada entidad a su DTO!
        when(projectMapper.toProjectDTO(project1)).thenReturn(dto1);
        when(projectMapper.toProjectDTO(project2)).thenReturn(dto2);


        // === When (Ejecución) ===
        Page<ProjectResponseDTO> result = projectService.getAllProjects(pageable);


        // === Then (Verificación) ===
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        // Verificamos que el contenido de la página son los DTOs que preparamos
        assertTrue(result.getContent().contains(dto1));
        assertTrue(result.getContent().contains(dto2));
    }

    @Test
    void getAllProjects_asAdminUser() {
        // Given
        // Mock authentication for an admin user
        Collection<GrantedAuthority> adminAuthorities = new ArrayList<GrantedAuthority>();
        adminAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        doReturn((Collection<? extends GrantedAuthority>) adminAuthorities).when(authentication).getAuthorities();

        Project project1 = createProjectEntity(1L, "Admin Project 1", "Description A", currentUser);
        Project project2 = createProjectEntity(2L, "Admin Project 2", "Description B", anotherUser);

        List<Project> allProjects = Arrays.asList(project1, project2);
        Page<Project> allProjectsPage = new PageImpl<>(allProjects, pageable, allProjects.size());

        when(projectRepository.findAll(pageable)).thenReturn(allProjectsPage);

        // When
        Page<ProjectResponseDTO> result = projectService.getAllProjects(pageable);

        // Then
        assertNotNull(result);
        assertEquals(allProjects.size(), result.getTotalElements());

        ProjectResponseDTO expectedDto1 = projectMapper.toProjectDTO(project1);
        ProjectResponseDTO actualDto1 = result.getContent().get(0);
        assertEquals(expectedDto1, actualDto1);

        ProjectResponseDTO expectedDto2 = projectMapper.toProjectDTO(project2);
        ProjectResponseDTO actualDto2 = result.getContent().get(1);
        assertEquals(expectedDto2, actualDto2);
    }

    @Test
    @DisplayName("Test para obtener un proyecto por su ID como dueño")
    void getProjectById_asRegularUser_owner() {
        // === Given (Preparación) ===
        Long projectId = 1L;

        // 1. Creamos las entidades y DTOs que usaremos
        Project projectEntity = createProjectEntity(projectId, "My Project", "Description", currentUser);
        ProjectResponseDTO expectedDto = new ProjectResponseDTO(projectId, "My Project", "Description", currentUser.getUsername(), List.of());

        // 2. Le damos su guion al Repositorio
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(projectEntity));

        // 3. ¡Le damos su guion al Mapper!
        when(projectMapper.toProjectDTO(projectEntity)).thenReturn(expectedDto);


        // === When (Ejecución) ===
        ProjectResponseDTO result = projectService.getProjectById(projectId);


        // === Then (Verificación) ===
        assertNotNull(result);
        // Ahora comparamos el resultado con el DTO que ya preparamos
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getName(), result.getName());
        // O puedes comparar el objeto completo si tienes .equals() y .hashCode() en tu DTO
        // assertEquals(expectedDto, result);
    }

    @Test
    @DisplayName("Test para obtener un proyecto por ID como ADMIN")
    void getProjectById_asAdminUser() {
        // === Given (Preparación) ===
        Long projectId = 1L;

        // 1. Creamos las entidades y DTOs
        Project projectEntity = createProjectEntity(projectId, "Another User's Project", "Description", anotherUser);
        ProjectResponseDTO expectedDto = new ProjectResponseDTO(projectId, "Another User's Project", "Description", anotherUser.getUsername(), List.of());

        // 2. Configuramos el rol de ADMIN
        Collection<GrantedAuthority> adminAuthorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        doReturn(adminAuthorities).when(authentication).getAuthorities();

        // 3. Le damos su guion al Repositorio
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(projectEntity));

        // 4. ¡Y le damos su guion al Mapper!
        when(projectMapper.toProjectDTO(projectEntity)).thenReturn(expectedDto);


        // === When (Ejecución) ===
        ProjectResponseDTO result = projectService.getProjectById(projectId);


        // === Then (Verificación) ===
        assertNotNull(result);
        // Comparamos el resultado con el DTO que preparamos
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getName(), result.getName());
        assertEquals(expectedDto.getOwnerUsername(), result.getOwnerUsername());
    }

    @Test
    void getProjectById_asRegularUser_notOwner_throwsException() {
        // Given
        Long projectId = 1L;
        Project project = createProjectEntity(projectId, "Another User's Project", "Description", anotherUser);

        doReturn(new ArrayList<GrantedAuthority>()).when(authentication).getAuthorities();
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> projectService.getProjectById(projectId));
    }

    @Test
    void getProjectById_notFound_throwsException() {
        // Given
        Long projectId = 99L;
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> projectService.getProjectById(projectId));
    }

    @Test
    @DisplayName("Test para actualizar un proyecto exitosamente por su dueño")
    void updateProject_success_byOwner() {
        // === Given (Preparación) ===
        Long projectId = 1L;
        CreateProjectRequestDTO updateRequest = new CreateProjectRequestDTO("Updated Project Name", "Updated Description");

        // 1. Creamos la entidad existente que el repo encontrará
        Project existingProject = createProjectEntity(projectId, "Original Project Name", "Original Description", currentUser);

        // 2. Creamos la entidad que el repo devolverá DESPUÉS de guardar
        Project updatedProjectEntity = createProjectEntity(projectId, updateRequest.getName(), updateRequest.getDescription(), currentUser);

        // 3. Creamos el DTO que esperamos como resultado final
        ProjectResponseDTO expectedResponseDTO = new ProjectResponseDTO(
                projectId,
                updateRequest.getName(),
                updateRequest.getDescription(),
                currentUser.getUsername(),
                Collections.emptyList()
        );

        // 4. Le damos sus guiones a los Mocks
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(projectRepository.save(any(Project.class))).thenReturn(updatedProjectEntity);
        // ¡Aquí está la línea que faltaba!
        when(projectMapper.toProjectDTO(updatedProjectEntity)).thenReturn(expectedResponseDTO);


        // === When (Ejecución) ===
        ProjectResponseDTO result = projectService.updateProject(projectId, updateRequest);


        // === Then (Verificación) ===
        assertNotNull(result);
        assertEquals(expectedResponseDTO.getId(), result.getId());
        assertEquals(expectedResponseDTO.getName(), result.getName());
        assertEquals(expectedResponseDTO.getDescription(), result.getDescription());
        assertEquals(expectedResponseDTO.getOwnerUsername(), result.getOwnerUsername());
    }

    @Test
    void updateProject_unauthorized_notOwner_throwsException() {
        // Given
        Long projectId = 1L;
        CreateProjectRequestDTO updateRequest = new CreateProjectRequestDTO("Updated Project Name", "Updated Description");

        Project existingProject = createProjectEntity(projectId, "Original Project Name", "Original Description", anotherUser);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> projectService.updateProject(projectId, updateRequest));
    }

    @Test
    void updateProject_notFound_throwsException() {
        // Given
        Long projectId = 99L;
        CreateProjectRequestDTO updateRequest = new CreateProjectRequestDTO("Updated Project Name", "Updated Description");

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> projectService.updateProject(projectId, updateRequest));
    }

    @Test
    void deleteProject_success_byOwner() {
        // Given
        Long projectId = 1L;

        Project existingProject = createProjectEntity(projectId, "Project to Delete", "Description", currentUser);
        existingProject.setStatus(Status.ACTIVE);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(projectRepository.save(any(Project.class))).thenReturn(existingProject); // Mock saving the updated project

        // When
        projectService.deleteProject(projectId);

        // Then
        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, times(1)).save(existingProject);
        assertEquals(Status.ARCHIVED, existingProject.getStatus());
    }

    @Test
    void deleteProject_unauthorized_notOwner_throwsException() {
        // Given
        Long projectId = 1L;

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("anotheruser");

        Project existingProject = createProjectEntity(projectId, "Project to Delete", "Description", anotherUser);
        existingProject.setStatus(Status.ACTIVE);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> projectService.deleteProject(projectId));

        // Verify that save was not called
        verify(projectRepository, times(0)).save(any(Project.class));
    }

    @Test
    void deleteProject_notFound_throwsException() {
        // Given
        Long projectId = 99L;

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> projectService.deleteProject(projectId));

        // Verify that save was not called
        verify(projectRepository, times(0)).save(any(Project.class));
    }
}
