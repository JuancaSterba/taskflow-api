package com.juancasterba.taskflow_api.controller;

import com.juancasterba.taskflow_api.model.Project;
import com.juancasterba.taskflow_api.model.Task;
import com.juancasterba.taskflow_api.service.ProjectService;
import com.juancasterba.taskflow_api.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final TaskService taskService;

    @GetMapping
    public List<Project> getAllProjects(){
        return projectService.getAllProjects();
    }

    @PostMapping
    public ResponseEntity<Project> createProject(@Valid @RequestBody Project project){
        Project createdProject = projectService.createProject(project);
        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id){
        return new ResponseEntity<>(projectService.getProjectById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @Valid @RequestBody Project projectDetails){
        Project updatedProject = projectService.updateProject(id, projectDetails);
        return new ResponseEntity<>(updatedProject, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProjectById(@PathVariable Long id){
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{projectId}/tasks")
    public ResponseEntity<Task> createTaskForProject(@PathVariable Long projectId, @Valid @RequestBody Task task){
        Task createdTask = taskService.createTaskForProject(projectId, task);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

}
