package com.juancasterba.taskflow_api.service;

import com.juancasterba.taskflow_api.exception.ResourceNotFoundException;
import com.juancasterba.taskflow_api.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskService {

    List<Task> getAllTasks();
    Task createTask(Task task);
    Task getTaskById (Long id) throws ResourceNotFoundException;
    Task updateTask (Long id, Task taskDetails) throws ResourceNotFoundException;
    void deleteById (Long id) throws ResourceNotFoundException;

}
