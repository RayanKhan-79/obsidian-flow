package com.example.backend.repositories;

import com.example.backend.database.Database;
import com.example.backend.models.ProjectManager;

public class ProjectManagerRepo extends RepositoryBase<ProjectManager> {

    public ProjectManagerRepo(Database dbService) 
    {
        super("Project_Managers", dbService, ProjectManager.class);
    }

    @Override
    protected String InsertQuery() 
    { 
        return String.format(
            "INSERT INTO %s (user_id) VALUES (?)",
            tableName 
        );
    }
}
