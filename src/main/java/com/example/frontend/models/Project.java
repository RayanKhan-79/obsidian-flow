package com.example.frontend.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Project {
    private int id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String color;
    private int ownerId; // Project Owner (Creator)
    private String ownerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status; // Active, Completed, On Hold, Archived
    
    // Team structure
    private List<ProjectMember> members;
    private Map<Integer, String> memberRoles; // userId -> role (Admin, Member, Viewer)
    
    // Tasks
    private List<Integer> taskIds;
    private int totalTasks;
    private int completedTasks;
    
    // Settings
    private boolean isPublic;
    private String defaultAssignee;
    private List<String> tags;
    
    public Project() {
        this.members = new ArrayList<>();
        this.memberRoles = new HashMap<>();
        this.taskIds = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = "Active";
    }
    
    public Project(String name, String description, LocalDate startDate, LocalDate endDate, String color, int ownerId, String ownerName) {
        this();
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.color = color;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }
    
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public List<ProjectMember> getMembers() { return members; }
    public void setMembers(List<ProjectMember> members) { this.members = members; }
    
    public void addMember(ProjectMember member) {
        this.members.add(member);
        this.memberRoles.put(member.getUserId(), member.getRole());
        this.updatedAt = LocalDateTime.now();
    }
    
    public void removeMember(int userId) {
        this.members.removeIf(m -> m.getUserId() == userId);
        this.memberRoles.remove(userId);
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getMemberRole(int userId) {
        return memberRoles.getOrDefault(userId, "None");
    }
    
    public List<Integer> getTaskIds() { return taskIds; }
    public void setTaskIds(List<Integer> taskIds) { this.taskIds = taskIds; }
    public void addTaskId(int taskId) { 
        this.taskIds.add(taskId);
        this.totalTasks++;
        this.updatedAt = LocalDateTime.now();
    }
    
    public int getTotalTasks() { return totalTasks; }
    public void setTotalTasks(int totalTasks) { this.totalTasks = totalTasks; }
    
    public int getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(int completedTasks) { this.completedTasks = completedTasks; }
    
    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean aPublic) { isPublic = aPublic; }
    
    public String getDefaultAssignee() { return defaultAssignee; }
    public void setDefaultAssignee(String defaultAssignee) { this.defaultAssignee = defaultAssignee; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public void addTag(String tag) { this.tags.add(tag); }
    
    public double getProgress() {
        if (totalTasks == 0) return 0;
        return (completedTasks * 100.0) / totalTasks;
    }
}