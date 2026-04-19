package com.example.backend.repositories;

import java.time.LocalDateTime;
import java.util.List;

import com.example.backend.database.Constants;
import com.example.backend.database.Database;
import com.example.backend.models.Task;
import com.example.backend.util.Util;

public class TaskRepo extends RepositoryBase<Task> 
{
    public TaskRepo(Database dbService) 
    {
        super("tasks", dbService, Task.class);
    }

    @Override
    protected String InsertQuery() 
    {
        return String.format("""
                INSERT INTO %s 
                (project_id, title, description, assigned_user_id, priority, status, due_date, created_date)
                VALUES
                (?, ?, ?, ?, ?, ?, ?, '%s')
            """, 
            tableName, 
            LocalDateTime.now().toString()
        );
    }

    public List<Task> GetAllByProject(Long projectId)
    {
        var result = dbService.executeQuery(
            String.format("SELECT * FROM %s WHERE %s = ?", tableName, Constants.PROJECT_ID), projectId
        ); 
        
        return Util.MapResultToModelList(result, Task.class);
    }

    public boolean UpdateTask(
        Long taskId,
        Long assignedUserId,
        Long priority,
        String status,
        String description,
        String dueDate
    ) {
        try {
            dbService.executeUpdate(
                String.format(
                    "UPDATE %s SET assigned_user_id = ?, priority = ?, status = ?, description = ?, due_date = ? WHERE id = ?",
                    tableName
                ),
                assignedUserId,
                priority,
                status,
                description,
                dueDate,
                taskId
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
