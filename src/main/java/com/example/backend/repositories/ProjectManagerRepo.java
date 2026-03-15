package com.example.backend.repositories;

import com.example.backend.models.ProjectManager;
import com.example.backend.services.database.DBService;

public class ProjectManagerRepo extends RepositoryBase<ProjectManager> {

    public ProjectManagerRepo(DBService dbService) 
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
