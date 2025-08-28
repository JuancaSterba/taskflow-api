package com.juancasterba.taskflow_api.service;

import com.juancasterba.taskflow_api.exception.ResourceNotFoundException;
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

    @Override
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Task getTaskById(Long id) throws ResourceNotFoundException {
        return taskRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException("No task found")
        );
    }

    @Override
    public Task updateTask(Long id, Task taskDetails) throws ResourceNotFoundException {
        Task task = taskRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("No task found")
        );
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setCompleted(taskDetails.isCompleted());
        return taskRepository.save(task);
    }

    @Override
    public void deleteById(Long id) throws ResourceNotFoundException {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("No task found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    @Override
    public Task createTaskForProject(Long projectId, Task task) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(()->new ResourceNotFoundException("No project found with id" + projectId));
        task.setProject(project);
        return taskRepository.save(task);
    }

}
