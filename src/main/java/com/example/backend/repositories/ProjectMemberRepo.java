package com.example.backend.repositories;

import com.example.backend.database.Database;
import com.example.backend.models.ProjectMember;

public class ProjectMemberRepo extends RepositoryBase<ProjectMember> 
{

    public ProjectMemberRepo(Database dbService) 
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
