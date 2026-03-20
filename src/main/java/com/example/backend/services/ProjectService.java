package com.example.backend.services;

import java.util.List;
import java.util.Optional;

import com.example.backend.database.Database;
import com.example.backend.enums.UserTypes;
import com.example.backend.models.Project;
import com.example.backend.models.ProjectDashboard;
import com.example.backend.models.Task;
import com.example.backend.repositories.ProjectMemberRepo;
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
    private final ProjectRepo projectRepo;
    private final UserRepo userRepo;
    private final ProjectMemberRepo projectMemberRepo;
    private final TaskRepo taskRepo;

    private ProjectService() {
        authService = AuthService.GetInstance();
        projectRepo = new ProjectRepo("Projects", Database.GetInstance());
        userRepo = new UserRepo(Database.GetInstance());
        projectMemberRepo = new ProjectMemberRepo(Database.GetInstance());
        taskRepo = new TaskRepo("Tasks", Database.GetInstance());
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

        if (!currentUser.userType.equals(UserTypes.PROJECT_MANAGER.toString()))
            return Optional.empty();

        if (title == null || title.trim().isEmpty())
            return Optional.empty();

        return projectRepo.Create(title.trim(), description, currentUser.Id);
    }

    public boolean addMemberToProject(Long projectId, Long memberUserId) {
        if (projectId == null || memberUserId == null)
            return false;

        var current = authService.currentUser;
        if (current == null || current.isEmpty())
            return false;

        var currentUser = current.get();
        if (!currentUser.userType.equals(UserTypes.PROJECT_MANAGER.toString()))
            return false;

        var projectOpt = projectRepo.Find(projectId);
        if (projectOpt.isEmpty())
            return false;

        var project = projectOpt.get();
        if (!project.manager_id.equals(currentUser.Id))
            return false;

        // Validate target user exists and is a member role
        var targetUserOpt = userRepo.Find(memberUserId);
        if (targetUserOpt.isEmpty())
            return false;

        var targetUser = targetUserOpt.get();
        if (!targetUser.userType.equals(UserTypes.PROJECT_MEMBER.toString()))
            return false;

        // Ensure we have a Project_Members entry for this user (needed for membership list/tracking)
        var membershipRecord = projectMemberRepo.findByUserId(memberUserId);
        if (membershipRecord.isEmpty()) {
            projectMemberRepo.Create(memberUserId);
        }

        // Prevent duplicate assignments
        if (projectRepo.isUserMember(projectId, memberUserId))
            return false;

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
