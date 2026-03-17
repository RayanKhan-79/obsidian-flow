package com.example.frontend.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Task {
    private int id;
    private String name;
    private String description;
    private String assignedTo;
    private int assignedToId;
    private String createdBy;
    private int createdById;
    private String priority; // High, Medium, Low
    private String status; // To Do, In Progress, Done, Blocked
    private LocalDate deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int projectId;
    private String project;
    private List<Comment> comments;
    private List<String> attachments;
    private double estimatedHours;
    private double actualHours;
    private List<Integer> watchers; // User IDs who are watching this task
    
    public Task() {
        this.comments = new ArrayList<>();
        this.attachments = new ArrayList<>();
        this.watchers = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Task(String name, String assignedTo, String priority, String status, LocalDate deadline) {
        this();
        this.name = name;
        this.assignedTo = assignedTo;
        this.priority = priority;
        this.status = status;
        this.deadline = deadline;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    
    public int getAssignedToId() { return assignedToId; }
    public void setAssignedToId(int assignedToId) { this.assignedToId = assignedToId; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public int getCreatedById() { return createdById; }
    public void setCreatedById(int createdById) { this.createdById = createdById; }
    
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    
    public LocalDate getDueDate() { return deadline; }
    public void setDueDate(LocalDate dueDate) { this.deadline = dueDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }
    
    public String getProject() { return project; }
    public void setProject(String project) { this.project = project; }
    
    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
    public void addComment(Comment comment) { 
        this.comments.add(comment);
        this.updatedAt = LocalDateTime.now();
    }
    
    public List<String> getAttachments() { return attachments; }
    public void setAttachments(List<String> attachments) { this.attachments = attachments; }
    public void addAttachment(String attachment) { this.attachments.add(attachment); }
    
    public double getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(double estimatedHours) { this.estimatedHours = estimatedHours; }
    
    public double getActualHours() { return actualHours; }
    public void setActualHours(double actualHours) { this.actualHours = actualHours; }
    
    public List<Integer> getWatchers() { return watchers; }
    public void setWatchers(List<Integer> watchers) { this.watchers = watchers; }
    public void addWatcher(int userId) { this.watchers.add(userId); }
    public void removeWatcher(int userId) { this.watchers.remove(Integer.valueOf(userId)); }
    
    @Override
    public String toString() {
        return name + " (" + status + ")";
    }
}