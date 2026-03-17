package com.example.frontend.utils;

import java.util.HashMap;
import java.util.Map;

import com.example.frontend.models.Project;
import com.example.frontend.models.Task;
import com.example.frontend.models.User;

public class SessionManager {
    private static User currentUser;
    private static Map<String, Object> sessionData = new HashMap<>();
    
    // Current user management
    public static void setCurrentUser(User user) {
        currentUser = user;
        sessionData.put("userId", user.getId());
        sessionData.put("userName", user.getFullName());
        sessionData.put("userRole", user.getRole());
        sessionData.put("userEmail", user.getEmail());
    }
    
    public static User getCurrentUser() {
        return currentUser;
    }
    
    public static void clearSession() {
        currentUser = null;
        sessionData.clear();
    }
    
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    
    // Role checks
    public static boolean isAdmin() {
        return currentUser != null && "Admin".equals(currentUser.getRole());
    }
    
    public static boolean isProjectManager() {
        return currentUser != null && 
               ("Admin".equals(currentUser.getRole()) || "Project Manager".equals(currentUser.getRole()));
    }
    
    public static boolean isMember() {
        return currentUser != null && "Member".equals(currentUser.getRole());
    }
    
    public static boolean isViewer() {
        return currentUser != null && "Viewer".equals(currentUser.getRole());
    }
    
    // Permission checks for various actions
    public static boolean canCreateProject() {
        return isAdmin() || isProjectManager();
    }
    
    public static boolean canEditProject() {
        return isAdmin() || isProjectManager();
    }
    
    public static boolean canDeleteProject() {
        return isAdmin(); // Only admins can delete projects
    }
    
    public static boolean canManageTeam() {
        return isAdmin() || isProjectManager();
    }
    
    public static boolean canAddTeamMembers() {
        return isAdmin() || isProjectManager();
    }
    
    public static boolean canRemoveTeamMembers() {
        return isAdmin() || isProjectManager();
    }
    
    public static boolean canCreateTask() {
        return isAdmin() || isProjectManager() || isMember();
    }
    
    public static boolean canEditTask(Task task) {
        if (isAdmin()) return true;
        if (isProjectManager()) return true;
        // Members can only edit tasks assigned to them
        return isMember() && task.getAssignedToId() == currentUser.getId();
    }
    
    public static boolean canDeleteTask() {
        return isAdmin() || isProjectManager();
    }
    
    public static boolean canCommentOnTask() {
        return isAdmin() || isProjectManager() || isMember();
    }
    
    public static boolean canViewReports() {
        return isAdmin() || isProjectManager();
    }
    
    public static boolean canViewAllTasks() {
        return isAdmin() || isProjectManager();
    }
    
    public static boolean canViewTeam() {
        return isAdmin() || isProjectManager() || isMember();
    }
    
    // Get dashboard type based on role
    public static String getDashboardType() {
        if (isAdmin()) return "admin";
        if (isProjectManager()) return "manager";
        if (isMember()) return "member";
        if (isViewer()) return "viewer";
        return "guest";
    }
    
    // Get welcome message based on role
    public static String getWelcomeMessage() {
        if (currentUser == null) return "Welcome!";
        
        switch(currentUser.getRole()) {
            case "Admin":
                return "Welcome back, " + currentUser.getFullName() + " (Administrator)";
            case "Project Manager":
                return "Welcome back, " + currentUser.getFullName() + " (Project Manager)";
            case "Member":
                return "Hello, " + currentUser.getFullName() + "! Here are your assigned tasks";
            case "Viewer":
                return "Welcome, " + currentUser.getFullName() + " (Viewer)";
            default:
                return "Welcome, " + currentUser.getFullName();
        }
    }
    
    // Session data storage
    public static void setData(String key, Object value) {
        sessionData.put(key, value);
    }
    
    public static Object getData(String key) {
        return sessionData.get(key);
    }
    
    public static void removeData(String key) {
        sessionData.remove(key);
    }
    
    // Current context
    private static Project currentProject;
    private static Task currentTask;
    
    public static void setCurrentProject(Project project) {
        currentProject = project;
        sessionData.put("currentProjectId", project != null ? project.getId() : null);
    }
    
    public static Project getCurrentProject() {
        return currentProject;
    }
    
    public static void setCurrentTask(Task task) {
        currentTask = task;
    }
    
    public static Task getCurrentTask() {
        return currentTask;
    }
}