package com.example.backend.models;

import java.util.Map;

public class ProjectDashboard {
    public Long projectId;
    public String projectTitle;
    public Map<String, Integer> statusCounts;
    public int totalTasks;
    public int completedTasks;
    public int pendingTasks;
    public double completionPercentage;

    public ProjectDashboard(Long projectId, String projectTitle, Map<String, Integer> statusCounts,
            int totalTasks, int completedTasks, int pendingTasks, double completionPercentage) {
        this.projectId = projectId;
        this.projectTitle = projectTitle;
        this.statusCounts = statusCounts;
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
        this.pendingTasks = pendingTasks;
        this.completionPercentage = completionPercentage;
    }
}
