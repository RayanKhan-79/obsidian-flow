package com.example.backend.repositories;

import java.util.Optional;

import com.example.backend.database.Database;
import com.example.backend.models.ProjectMember;
import com.example.backend.util.Util;

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

    public Optional<ProjectMember> findByUserId(Long userId)
    {
        try 
        {
            var result = dbService.executeQuery("""
                    SELECT u.*, pm.user_id
                    FROM Project_Members pm
                    JOIN Users u
                    ON pm.user_id = u.Id
                    WHERE pm.user_id = ?              
                """,
                userId
            );
            return Util.MapResultToModel(result, ProjectMember.class);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
