package com.example.backend.repositories;

import java.time.LocalDateTime;

import com.example.backend.models.Project;
import com.example.backend.services.database.DBService;

public class ProjectRepo extends RepositoryBase<Project> 
{
    public ProjectRepo(String tableName, DBService dbService) 
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
