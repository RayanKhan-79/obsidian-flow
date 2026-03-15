package com.example.backend.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import com.example.backend.enums.TaskStatus;
import com.example.backend.services.database.Constants;


public class Task 
{
    public Long Id;
    public String title;
    public String description;
    public Long priority;
    public TaskStatus status;
    public LocalDateTime dueDate;
    public LocalDateTime createdDate;
    
    public Task(Long Id, String title, String description, Long priority, TaskStatus status, LocalDateTime dueDate, LocalDateTime createdDate) 
    {
        this.Id = Id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.dueDate = dueDate;
        this.createdDate = createdDate;
    }

    public Task(ResultSet rs)
    {
        try 
        {
            Id = rs.getLong(Constants.Id);
            title = rs.getString(Constants.Title);
            description = rs.getString(Constants.Description);
            status = Enum.valueOf(TaskStatus.class, rs.getString(Constants.Status));
            priority = rs.getLong(Constants.Priority);
            dueDate = LocalDateTime.parse(rs.getString(Constants.Due_Date));
            createdDate = LocalDateTime.parse(rs.getString(Constants.Created_Date));            

        } catch (SQLException e) {
            System.out.println("Task Parsing Error");
            System.exit(1);
        }
    }
}
