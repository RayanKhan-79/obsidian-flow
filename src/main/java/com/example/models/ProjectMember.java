package com.example.models;

import java.time.LocalDateTime;

public class ProjectMember {
    private int userId;
    private String userName;
    private String userEmail;
    private String role; // Admin, Member, Viewer
    private LocalDateTime joinedAt;
    private int assignedTasksCount;
    private int completedTasksCount;
    
    public ProjectMember() {
        this.joinedAt = LocalDateTime.now();
        this.assignedTasksCount = 0;
        this.completedTasksCount = 0;
    }
    
    public ProjectMember(int userId, String userName, String userEmail, String role) {
        this();
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.role = role;
    }
    
    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
    
    public int getAssignedTasksCount() { return assignedTasksCount; }
    public void setAssignedTasksCount(int assignedTasksCount) { this.assignedTasksCount = assignedTasksCount; }
    public void incrementAssignedTasks() { this.assignedTasksCount++; }
    public void decrementAssignedTasks() { this.assignedTasksCount--; }
    
    public int getCompletedTasksCount() { return completedTasksCount; }
    public void setCompletedTasksCount(int completedTasksCount) { this.completedTasksCount = completedTasksCount; }
    public void incrementCompletedTasks() { this.completedTasksCount++; }
    
    @Override
    public String toString() {
        return userName + " (" + role + ")";
    }
}