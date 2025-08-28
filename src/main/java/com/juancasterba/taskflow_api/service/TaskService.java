package com.juancasterba.taskflow_api.service;

import com.juancasterba.taskflow_api.exception.ResourceNotFoundException;
import com.juancasterba.taskflow_api.model.Task;

import java.util.List;

public interface TaskService {

    Task createTask(Task task);
    List<Task> getAllTasks();
    Task getTaskById (Long id) throws ResourceNotFoundException;
    Task updateTask (Long id, Task taskDetails) throws ResourceNotFoundException;
    void deleteById (Long id) throws ResourceNotFoundException;
    Task createTaskForProject (Long projectId, Task task);

}
