package com.juancasterba.taskflow_api.service;

import com.juancasterba.taskflow_api.dto.CreateTaskRequestDTO;
import com.juancasterba.taskflow_api.dto.TaskResponseDTO;
import com.juancasterba.taskflow_api.dto.events.TaskEventDTO;
import com.juancasterba.taskflow_api.exception.ResourceNotFoundException;
import com.juancasterba.taskflow_api.mapper.ProjectMapper;
import com.juancasterba.taskflow_api.model.Project;
import com.juancasterba.taskflow_api.model.Task;
import com.juancasterba.taskflow_api.repository.ProjectRepository;
import com.juancasterba.taskflow_api.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService{

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @Transactional
    public TaskResponseDTO createTask(CreateTaskRequestDTO taskDTO) {
        Task task = projectMapper.toTaskEntity(taskDTO);
        return projectMapper.toTaskDTO(taskRepository.save(task));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getAllTasks() {
        return taskRepository.findAll().stream().map(projectMapper::toTaskDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponseDTO getTaskById(Long id) throws ResourceNotFoundException {
        return projectMapper.toTaskDTO(taskRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException("No task found"))
        );
    }

    @Override
    @Transactional
    public TaskResponseDTO updateTask(Long id, CreateTaskRequestDTO taskDTO) throws ResourceNotFoundException {
        Task task = taskRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("No task found")
        );
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setCompleted(taskDTO.isCompleted());
        return projectMapper.toTaskDTO(taskRepository.save(task));
    }

    @Override
    @Transactional
    public void deleteById(Long id) throws ResourceNotFoundException {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("No task found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    @Override
    @Transactional
    public TaskResponseDTO createTaskForProject(Long projectId, CreateTaskRequestDTO taskDTO) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(()->new ResourceNotFoundException("No project found with id" + projectId));
        Task task = projectMapper.toTaskEntity(taskDTO);
        task.setProject(project);
        Task savedTask = taskRepository.save(task);
        TaskEventDTO event = new TaskEventDTO(
                savedTask.getId(),
                savedTask.getTitle(),
                project.getId(),
                project.getName(),
                project.getOwner().getUsername(),
                project.getOwner().getEmail()
        );

        // ¡Y confirma que estás enviando el objeto 'event'!
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send("tasks-events", event);

        return projectMapper.toTaskDTO(savedTask);
    }

}
