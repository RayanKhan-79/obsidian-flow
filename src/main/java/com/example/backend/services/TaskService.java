package com.example.backend.services;

import java.time.LocalDateTime;
import java.util.Optional;

import com.example.backend.database.Database;
import com.example.backend.enums.TaskStatus;
import com.example.backend.models.Task;
import com.example.backend.repositories.ProjectRepo;
import com.example.backend.repositories.TaskRepo;

public class TaskService {

    private static TaskService instance;

    public static TaskService GetInstance() {
        if (instance == null)
            instance = new TaskService();
        return instance;
    }

    private final AuthService authService;
    private final ProjectRepo projectRepo;
    private final TaskRepo taskRepo;

    private TaskService() {
        authService = AuthService.GetInstance();
        projectRepo = new ProjectRepo("Projects", Database.GetInstance());
        taskRepo = new TaskRepo("Tasks", Database.GetInstance());
    }

    /**
     * Creates a task for a specific project.
     *
     * Preconditions:
     *   - User must be authenticated and must belong to the project (manager or member)
     *   - Project must exist
     *   - Title must be non-empty
     */
    public Optional<Task> createTask(Long projectId, String title, String description, Long priority, LocalDateTime dueDate) {
        if (projectId == null || title == null || title.trim().isEmpty())
            return Optional.empty();

        var currentUser = authService.currentUser;
        if (currentUser == null || currentUser.isEmpty())
            return Optional.empty();

        // Verify project exists and user is part of it
        var projectOpt = projectRepo.Find(projectId);
        if (projectOpt.isEmpty())
            return Optional.empty();

        var current = currentUser.get();
        boolean isManager = projectOpt.get().manager_id.equals(current.Id);
        boolean isMember = projectRepo.isUserMember(projectId, current.Id);
        if (!isManager && !isMember)
            return Optional.empty();

        return taskRepo.Create(
            projectId,
            title.trim(),
            description,
            priority,
            TaskStatus.PENDING.toString(),
            dueDate != null ? dueDate.toString() : null
        );
    }
}
