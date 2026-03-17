package com.example.backend.repositories;

import java.time.LocalDateTime;
import java.util.List;

import com.example.backend.models.Task;
import com.example.backend.services.database.Constants;
import com.example.backend.services.database.DBService;
import com.example.backend.util.Util;

public class TaskRepo extends RepositoryBase<Task> 
{
    public TaskRepo(String tableName, DBService dbService) 
    {
        super("Tasks", dbService, Task.class);
    }

    @Override
    protected String InsertQuery() 
    {
        return String.format("""
                INSERT INTO %s 
                (project_id, title, description, priority, status, due_date, created_date)
                VALUES
                (?, ?, ?, ?, ?, ?, %s)
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
}
