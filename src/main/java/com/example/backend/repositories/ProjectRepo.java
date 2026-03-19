package com.example.backend.repositories;

import java.time.LocalDateTime;

import com.example.backend.database.Database;
import com.example.backend.models.Project;

public class ProjectRepo extends RepositoryBase<Project> 
{
    public ProjectRepo(String tableName, Database dbService) 
    {
        super("Projects", dbService, Project.class);
    }

    @Override
    protected String InsertQuery() 
    {
        return String.format(
            "INSERT INTO Projects (title, manager_id, created_date) VALUES (?, ?, %s)",
            LocalDateTime.now().toString()
        );
    }
    
}
