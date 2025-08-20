package com.juancasterba.taskflow_api.service;

import com.juancasterba.taskflow_api.model.Task;

import java.util.List;

public interface TaskService {

    List<Task> getAllTasks();
    Task createTask(Task task);

}
