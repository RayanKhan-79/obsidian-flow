package com.example.backend.repositories;

import java.time.LocalDateTime;

import com.example.backend.models.Task;
import com.example.backend.services.database.DBService;

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


}
