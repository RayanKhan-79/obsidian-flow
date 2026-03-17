package com.example.frontend.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Notification {
    private int id;
    private int userId;
    private String type; // "mention", "reply", "task_assigned", "status_change", "deadline"
    private String title;
    private String message;
    private String relatedItem; // task name, project name, etc.
    private int relatedId; // taskId, projectId, commentId
    private boolean isRead;
    private boolean isActionable;
    private String actionType; // "view_task", "view_project", "reply_comment"
    private LocalDateTime createdAt;
    
    public Notification() {
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }
    
    public Notification(int userId, String type, String title, String message, String relatedItem, int relatedId) {
        this();
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.relatedItem = relatedItem;
        this.relatedId = relatedId;
        
        // Set action type based on notification type
        switch(type) {
            case "mention":
            case "reply":
                this.actionType = "view_task";
                this.isActionable = true;
                break;
            case "task_assigned":
                this.actionType = "view_task";
                this.isActionable = true;
                break;
            case "deadline":
                this.actionType = "view_task";
                this.isActionable = true;
                break;
            default:
                this.isActionable = false;
        }
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getRelatedItem() { return relatedItem; }
    public void setRelatedItem(String relatedItem) { this.relatedItem = relatedItem; }
    
    public int getRelatedId() { return relatedId; }
    public void setRelatedId(int relatedId) { this.relatedId = relatedId; }
    
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    
    public boolean isActionable() { return isActionable; }
    public void setActionable(boolean actionable) { isActionable = actionable; }
    
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getTimeAgo() {
        LocalDateTime now = LocalDateTime.now();
        long seconds = java.time.Duration.between(createdAt, now).getSeconds();
        
        if (seconds < 60) return seconds + " seconds ago";
        if (seconds < 3600) return (seconds / 60) + " minutes ago";
        if (seconds < 86400) return (seconds / 3600) + " hours ago";
        if (seconds < 604800) return (seconds / 86400) + " days ago";
        
        return createdAt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }
    
    public String getIcon() {
        switch(type) {
            case "mention": return "🔔";
            case "reply": return "💬";
            case "task_assigned": return "📋";
            case "status_change": return "🔄";
            case "deadline": return "⏰";
            case "comment": return "💭";
            default: return "📌";
        }
    }
    
    public String getColor() {
        switch(type) {
            case "mention": return "#8b5cf6"; // Purple
            case "reply": return "#3b82f6"; // Blue
            case "task_assigned": return "#10b981"; // Green
            case "status_change": return "#f59e0b"; // Orange
            case "deadline": return "#ef4444"; // Red
            default: return "#6366f1"; // Indigo
        }
    }
}