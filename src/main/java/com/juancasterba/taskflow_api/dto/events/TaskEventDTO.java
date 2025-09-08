package com.juancasterba.taskflow_api.dto.events;

public record TaskEventDTO(
        Long taskId,
        String taskTitle,
        Long projectId,
        String projectName,
        String ownerUsername,
        String ownerEmail
) {}