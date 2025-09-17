package com.juancasterba.taskflow_api.service;

import com.juancasterba.taskflow_api.config.SecurityUtils;
import com.juancasterba.taskflow_api.dto.CreateTaskRequestDTO;
import com.juancasterba.taskflow_api.dto.TaskResponseDTO;
import com.juancasterba.taskflow_api.dto.events.TaskEventDTO;
import com.juancasterba.taskflow_api.exception.ResourceNotFoundException;
import com.juancasterba.taskflow_api.mapper.TaskMapper;
import com.juancasterba.taskflow_api.model.Project;
import com.juancasterba.taskflow_api.model.Status;
import com.juancasterba.taskflow_api.model.Task;
import com.juancasterba.taskflow_api.repository.ProjectRepository;
import com.juancasterba.taskflow_api.repository.TaskRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link TaskService} interface.
 * This class contains the business logic for managing tasks, including creation,
 * retrieval, updates, and archival. It ensures that all operations respect
 * user permissions by checking project ownership or admin privileges.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService{

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TaskMapper taskMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SecurityUtils securityUtils;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public TaskResponseDTO createTaskForProject(Long projectId, CreateTaskRequestDTO taskDTO) {
        var currentUser = securityUtils.getCurrentAuthenticatedUser();
        Project project = findProjectByIdOrThrow(projectId);

        // Authorization check: Only the project owner or an admin can add tasks.
        securityUtils.checkOwnershipOrAdmin(project, currentUser);

        Task task = taskMapper.toTaskEntity(taskDTO);
        task.setProject(project);
        Task savedTask = taskRepository.save(task);

        // Asynchronously publish an event to Kafka about the new task creation.
        TaskEventDTO event = new TaskEventDTO(
                savedTask.getId(), savedTask.getTitle(), project.getId(),
                project.getName(), project.getOwner().getUsername(), project.getOwner().getEmail()
        );

        // Send to Kafka and log the result for traceability.
        kafkaTemplate.send("tasks-events", event).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Successfully sent TaskEvent for task ID: {}", savedTask.getId());
            } else {
                log.error("Failed to send TaskEvent for task ID: {}. Reason: {}", savedTask.getId(), ex.getMessage());
            }
        });

        return taskMapper.toTaskDTO(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> getTasksByProjectId(Long projectId, Pageable pageable) {
        var currentUser = securityUtils.getCurrentAuthenticatedUser();
        Project project = findProjectByIdOrThrow(projectId);

        // Authorization check: A user can see tasks if they are the project owner or an admin.
        securityUtils.checkOwnershipOrAdmin(project, currentUser);

        Page<Task> taskPage = taskRepository.findByProject(project, pageable);
        return taskPage.map(taskMapper::toTaskDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponseDTO getTaskById(Long id) {
        var currentUser = securityUtils.getCurrentAuthenticatedUser();
        Task task = findTaskByIdOrThrow(id);

        // Authorization check: A user can see a task if they are the project owner or an admin.
        securityUtils.checkOwnershipOrAdmin(task.getProject(), currentUser);

        return taskMapper.toTaskDTO(task);
    }

    @Override
    @Transactional
    public TaskResponseDTO updateTask(Long id, CreateTaskRequestDTO taskDTO) {
        var currentUser = securityUtils.getCurrentAuthenticatedUser();
        Task task = findTaskByIdOrThrow(id);

        // Authorization check: A user can update a task if they are the project owner or an admin.
        securityUtils.checkOwnershipOrAdmin(task.getProject(), currentUser);

        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setCompleted(taskDTO.isCompleted());

        return taskMapper.toTaskDTO(taskRepository.save(task));
    }

    @Override
    @Transactional
    public void archiveTask(Long id) {
        var currentUser = securityUtils.getCurrentAuthenticatedUser();
        Task task = findTaskByIdOrThrow(id);

        // Authorization check: A user can archive a task if they are the project owner or an admin.
        securityUtils.checkOwnershipOrAdmin(task.getProject(), currentUser);

        task.setStatus(Status.ARCHIVED);
        taskRepository.save(task);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void hardDeleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public Page<TaskResponseDTO> findAllTasksForAdmin(Pageable pageable) {
        Session session = entityManager.unwrap(Session.class);
        session.disableFilter("activeStatusFilter"); // Desactivamos el filtro

        Page<Task> taskPage = taskRepository.findAll(pageable);

        return taskPage.map(taskMapper::toTaskDTO);
    }

    /**
     * Fetches a task by its ID from the repository.
     *
     * @param id The ID of the task to find.
     * @return The found {@link Task} entity.
     * @throws ResourceNotFoundException if no task is found with the given ID.
     */
    private Task findTaskByIdOrThrow(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }

    /**
     * Fetches a project by its ID from the repository.
     *
     * @param id The ID of the project to find.
     * @return The found {@link Project} entity.
     * @throws ResourceNotFoundException if no project is found with the given ID.
     */
    private Project findProjectByIdOrThrow(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
    }
}
