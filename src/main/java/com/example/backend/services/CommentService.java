package com.example.backend.services;

import java.time.LocalDateTime;
import java.util.Optional;

import com.example.backend.database.Database;
import com.example.backend.models.Comment;
import com.example.backend.models.Task;
import com.example.backend.repositories.CommentRepo;
import com.example.backend.repositories.TaskRepo;

public class CommentService {

    private static CommentService instance;

    public static CommentService GetInstance() {
        if (instance == null)
            instance = new CommentService();
        return instance;
    }

    private final AuthService authService;
    private final TaskRepo taskRepo;
    private final CommentRepo commentRepo;

    private CommentService() {
        authService = AuthService.GetInstance();
        taskRepo = new TaskRepo(Database.GetInstance());
        commentRepo = new CommentRepo(Database.GetInstance());
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
        boolean canComment = ProjectService.GetInstance().isUserInProject(task.project_id, currentUser.get().Id);
        if (!canComment)
            return Optional.empty();

        return commentRepo.Create(
            taskId,
            currentUser.get().Id,
            text.trim(),
            LocalDateTime.now().toString()
        );
    }
}
