package com.example.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Comment {
    private int id;
    private int taskId;
    private int userId;
    private String userName;
    private String userAvatar;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Integer> mentions;
    private List<Comment> replies;
    private Integer parentCommentId;
    private boolean isEdited;
    private int likeCount;
    private List<Integer> likedBy;
    
    public Comment() {
        this.createdAt = LocalDateTime.now();
        this.mentions = new ArrayList<>();
        this.replies = new ArrayList<>();
        this.likedBy = new ArrayList<>();
        this.isEdited = false;
        this.likeCount = 0;
    }
    
    public Comment(int taskId, int userId, String userName, String content) {
        this();
        this.taskId = taskId;
        this.userId = userId;
        this.userName = userName;
        this.content = content;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getUserAvatar() { return userAvatar; }
    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }
    
    public String getContent() { return content; }
    public void setContent(String content) { 
        this.content = content;
        this.updatedAt = LocalDateTime.now();
        this.isEdited = true;
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<Integer> getMentions() { return mentions; }
    public void setMentions(List<Integer> mentions) { this.mentions = mentions; }
    public void addMention(int userId) { this.mentions.add(userId); }
    
    public List<Comment> getReplies() { return replies; }
    public void setReplies(List<Comment> replies) { this.replies = replies; }
    public void addReply(Comment reply) { 
        this.replies.add(reply);
    }
    
    public Integer getParentCommentId() { return parentCommentId; }
    public void setParentCommentId(Integer parentCommentId) { this.parentCommentId = parentCommentId; }
    
    public boolean isEdited() { return isEdited; }
    public void setEdited(boolean edited) { isEdited = edited; }
    
    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }
    
    public List<Integer> getLikedBy() { return likedBy; }
    public void setLikedBy(List<Integer> likedBy) { this.likedBy = likedBy; }
    public void addLike(int userId) { 
        if (!likedBy.contains(userId)) {
            likedBy.add(userId);
            likeCount++;
        }
    }
    public void removeLike(int userId) { 
        if (likedBy.contains(userId)) {
            likedBy.remove(Integer.valueOf(userId));
            likeCount--;
        }
    }
    public boolean isLikedBy(int userId) { return likedBy.contains(userId); }
    
    public String getFormattedTime() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt.toLocalDate().equals(now.toLocalDate())) {
            long hours = java.time.Duration.between(createdAt, now).toHours();
            if (hours < 1) {
                long minutes = java.time.Duration.between(createdAt, now).toMinutes();
                return minutes + " min" + (minutes != 1 ? "s" : "") + " ago";
            }
            return hours + " hour" + (hours != 1 ? "s" : "") + " ago";
        } else if (createdAt.toLocalDate().equals(now.minusDays(1).toLocalDate())) {
            return "Yesterday at " + String.format("%02d:%02d", createdAt.getHour(), createdAt.getMinute());
        } else {
            return createdAt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm"));
        }
    }
    
    public String getInitials() {
        if (userName == null || userName.isEmpty()) return "?";
        String[] parts = userName.split(" ");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }
}