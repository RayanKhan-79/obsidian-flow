package com.example.models;

import java.time.LocalDate;

public class Task {

    private String name;
    private String assignedTo;
    private String priority;
    private String status;
    private LocalDate deadline;

    public Task(String name, String assignedTo, String priority, String status, LocalDate deadline) {
        this.name = name;
        this.assignedTo = assignedTo;
        this.priority = priority;
        this.status = status;
        this.deadline = deadline;
    }

    public String getName() { return name; }
    public String getAssignedTo() { return assignedTo; }
    public String getPriority() { return priority; }
    public String getStatus() { return status; }
    public LocalDate getDeadline() { return deadline; }

}