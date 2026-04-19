package com.example.backend.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import com.example.backend.database.Constants;
import com.example.backend.enums.TaskStatus;


public class Task 
{
    public Long Id;
    public Long project_id;
    public String title;
    public String description;
    public Long assignedUserId;
    public Long priority;
    public TaskStatus status;
    public LocalDateTime dueDate;
    public LocalDateTime createdDate;

    public Task(ResultSet rs)
    {
        try 
        {
            Id = rs.getLong(Constants.ID);
            project_id = rs.getLong(Constants.PROJECT_ID);
            title = rs.getString(Constants.TITLE);
            description = rs.getString(Constants.DESCRIPTION);
            var rawAssignedUserId = rs.getObject(Constants.ASSIGNED_USER_ID);
            assignedUserId = rawAssignedUserId == null ? null : ((Number) rawAssignedUserId).longValue();
            status = Enum.valueOf(TaskStatus.class, rs.getString(Constants.STATUS));
            priority = rs.getLong(Constants.PRIORITY);
            var dueDateRaw = rs.getString(Constants.DUE_DATE);
            dueDate = dueDateRaw == null || dueDateRaw.isBlank() ? null : LocalDateTime.parse(dueDateRaw);
            createdDate = LocalDateTime.parse(rs.getString(Constants.CREATED_DATE));            

        } catch (SQLException e) {
            System.out.println("Task Parsing Error");
            System.exit(1);
        }
    }
}
