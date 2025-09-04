package com.juancasterba.taskflow_api.service;

import com.juancasterba.taskflow_api.dto.CreateTaskRequestDTO;
import com.juancasterba.taskflow_api.dto.TaskResponseDTO;
import com.juancasterba.taskflow_api.exception.ResourceNotFoundException;
import com.juancasterba.taskflow_api.mapper.ProjectMapper;
import com.juancasterba.taskflow_api.model.Project;
import com.juancasterba.taskflow_api.model.Task;
import com.juancasterba.taskflow_api.repository.ProjectRepository;
import com.juancasterba.taskflow_api.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService{

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Override
    public TaskResponseDTO createTask(CreateTaskRequestDTO taskDTO) {
        Task task = projectMapper.toTaskEntity(taskDTO);
        return projectMapper.toTaskDTO(taskRepository.save(task));
    }

    @Override
    public List<TaskResponseDTO> getAllTasks() {
        return taskRepository.findAll().stream().map(projectMapper::toTaskDTO).toList();
    }

    @Override
    public TaskResponseDTO getTaskById(Long id) throws ResourceNotFoundException {
        return projectMapper.toTaskDTO(taskRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException("No task found"))
        );
    }

    @Override
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
    public void deleteById(Long id) throws ResourceNotFoundException {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("No task found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    @Override
    public TaskResponseDTO createTaskForProject(Long projectId, CreateTaskRequestDTO taskDTO) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(()->new ResourceNotFoundException("No project found with id" + projectId));
        Task task = projectMapper.toTaskEntity(taskDTO);
        task.setProject(project);
        return projectMapper.toTaskDTO(taskRepository.save(task));
    }

}
