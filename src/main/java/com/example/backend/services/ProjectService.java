package com.example.backend.services;

import java.util.List;
import java.util.Optional;

import com.example.backend.database.Database;
import com.example.backend.enums.Permissions;
import com.example.backend.models.Project;
import com.example.backend.models.ProjectDashboard;
import com.example.backend.models.Task;
import com.example.backend.repositories.ProjectRepo;
import com.example.backend.repositories.TaskRepo;
import com.example.backend.repositories.UserRepo;

public class ProjectService {

    private static ProjectService instance;

    public static ProjectService GetInstance() {
        if (instance == null)
            instance = new ProjectService();
        return instance;
    }

    private final AuthService authService;
    private final ActivityLogService logService;
    private final ProjectRepo projectRepo;
    private final UserRepo userRepo;
    private final TaskRepo taskRepo;

    private ProjectService() {
        authService = AuthService.GetInstance();
        logService = ActivityLogService.GetInstance();
        projectRepo = new ProjectRepo(Database.GetInstance());
        userRepo = new UserRepo(Database.GetInstance());
        taskRepo = new TaskRepo(Database.GetInstance());
    }

    /**
     * Creates a new project and assigns the current user as the project manager.
     *
     * @param title       required project title
     * @param description optional project description
     * @return the created project, or empty if creation failed or preconditions not met
     */
    public Optional<Project> createProject(String title, String description) {
        if (authService.currentUser == null || authService.currentUser.isEmpty())
            return Optional.empty();

        var currentUser = authService.currentUser.get();
        if (currentUser == null || currentUser.Id == null)
            return Optional.empty();

        if (!userRepo.HasPermission(currentUser.Id, Permissions.PROJECT_MANAGER))
            return Optional.empty();

        if (title == null || title.trim().isEmpty())
            return Optional.empty();

        
        Project project = projectRepo.Create(title.trim(), description, currentUser.Id).get();
        
        logService.addLogMessage(String.format(
            "%s created a new project \"%s\"",
        currentUser.email, project.title));

        return Optional.of(project);
    }

    public boolean addMemberToProject(Long projectId, Long memberUserId) {
        if (projectId == null || memberUserId == null)
            return false;

        var current = authService.currentUser;
        if (current == null || current.isEmpty())
            return false;

        var currentUser = current.get();
        if (!userRepo.HasPermission(memberUserId, Permissions.PROJECT_MANAGER))
            return false;

        var projectOpt = projectRepo.Find(projectId);
        if (projectOpt.isEmpty())
            return false;

        var project = projectOpt.get();
        if (!project.manager_id.equals(currentUser.Id))
            return false;

        // Validate target user exists
        var targetUserOpt = userRepo.Find(memberUserId);
        if (targetUserOpt.isEmpty())
            return false;

        // Prevent duplicate assignments
        if (projectRepo.isUserMember(projectId, memberUserId))
            return false;

        var member = userRepo.Find(memberUserId).get();

        logService.addLogMessage(String.format(
            "%s added %s to project \"%s\"",
            currentUser.email, member.email, project.title
        ));

        return projectRepo.addMemberToProject(projectId, memberUserId);
    }

    public boolean isUserInProject(Long projectId, Long userId) {
        if (projectId == null || userId == null)
            return false;

        var projectOpt = projectRepo.Find(projectId);
        if (projectOpt.isEmpty())
            return false;

        var project = projectOpt.get();
        if (project.manager_id.equals(userId))
            return true;

        return projectRepo.isUserMember(projectId, userId);
    }

    public Optional<ProjectDashboard> getProjectDashboard(Long projectId) {
        if (projectId == null)
            return Optional.empty();

        var current = authService.currentUser;
        if (current == null || current.isEmpty())
            return Optional.empty();

        var user = current.get();
        if (!isUserInProject(projectId, user.Id))
            return Optional.empty();

        var projectOpt = projectRepo.Find(projectId);
        if (projectOpt.isEmpty())
            return Optional.empty();

        var project = projectOpt.get();

        List<Task> tasks = taskRepo.GetAllByProject(projectId);
        int totalTasks = tasks.size();
        int completed = 0;
        int pending = 0;

        for (var task : tasks) {
            if (task == null || task.status == null)
                continue;
            switch (task.status) {
                case COMPLETED:
                    completed++;
                    break;
                case PENDING:
                default:
                    pending++;
                    break;
            }
        }

        double completionPercentage = 0;
        if (totalTasks > 0)
            completionPercentage = (completed * 100.0) / totalTasks;

        var statusCounts = java.util.Map.of(
            "COMPLETED", completed,
            "PENDING", pending
        );

        return Optional.of(new ProjectDashboard(
            project.Id,
            project.title,
            statusCounts,
            totalTasks,
            completed,
            pending,
            completionPercentage
        ));
    }
}
