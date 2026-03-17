package com.example.backend.repositories;

import com.example.backend.models.ProjectMember;
import com.example.backend.services.database.DBService;

public class ProjectMemberRepo extends RepositoryBase<ProjectMember> 
{

    public ProjectMemberRepo(DBService dbService) 
    {
        super("Project_Members", dbService, ProjectMember.class);
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
