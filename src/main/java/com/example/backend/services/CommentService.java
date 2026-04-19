package com.example.backend.services;

import java.time.LocalDateTime;
import java.util.Optional;

import com.example.backend.database.Database;
import com.example.backend.models.Comment;
import com.example.backend.models.Project;
import com.example.backend.models.Task;
import com.example.backend.repositories.CommentRepo;
import com.example.backend.repositories.ProjectRepo;
import com.example.backend.repositories.TaskRepo;

public class CommentService {

    private static CommentService instance;

    public static CommentService GetInstance() {
        if (instance == null)
            instance = new CommentService();
        return instance;
    }

    private final AuthService authService;
    private final ActivityLogService logService;
    private final ProjectService projectService;
    private final TaskRepo taskRepo;
    private final CommentRepo commentRepo;
    private final ProjectRepo projectRepo;

    private CommentService() {
        authService = AuthService.GetInstance();
        logService = ActivityLogService.GetInstance();
        projectService = ProjectService.GetInstance();
        taskRepo = new TaskRepo(Database.GetInstance());
        commentRepo = new CommentRepo(Database.GetInstance());
        projectRepo = new ProjectRepo(Database.GetInstance());
    }

    /**
     * Adds a comment to a task.
     *
     * Preconditions:
     *   - User must be authenticated and belong to the task's project
     *   - Task must exist
     *   - Comment text must be non-empty
     */
    public Optional<Comment> addCommentToTask(Long taskId, String text) {
        if (taskId == null || text == null || text.trim().isEmpty())
            return Optional.empty();

        var currentUser = authService.currentUser;
        if (currentUser == null || currentUser.isEmpty())
            return Optional.empty();

        var taskOpt = taskRepo.Find(taskId);
        if (taskOpt.isEmpty())
            return Optional.empty();

        Task task = taskOpt.get();

        // Ensure user is part of the project before commenting
        boolean canComment = projectService.isUserInProject(task.project_id, currentUser.get().Id);
        if (!canComment)
            return Optional.empty();

        Project project = projectRepo.Find(task.project_id).get();

        logService.addLogMessage(String.format(
            "%s added comment \"%s\" on task %s on project %s",
            currentUser.get().email, text, task.title, project.title
        ));

        return commentRepo.Create(
            taskId,
            currentUser.get().Id,
            text.trim(),
            LocalDateTime.now().toString()
        );
    }
}
