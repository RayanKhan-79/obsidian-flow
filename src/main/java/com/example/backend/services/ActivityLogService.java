package com.example.backend.services;

public class ActivityLogService {

    private static ActivityLogService instance;

    public static ActivityLogService GetInstance() {
        if (instance == null)
            instance = new ActivityLogService();
        return instance;
    }

    private ActivityLogService() {
    }

    // Placeholder methods for activity logging (to be implemented later)

    public void logProjectCreated(Long projectId, Long userId) {
        // TODO: record that a project was created
    }

    public void logMemberAdded(Long projectId, Long userId, Long addedUserId) {
        // TODO: record that a member was added to a project
    }

    public void logTaskCreated(Long taskId, Long projectId, Long userId) {
        // TODO: record that a task was created
    }

    public void logCommentAdded(Long commentId, Long taskId, Long userId) {
        // TODO: record that a comment was added
    }
}
