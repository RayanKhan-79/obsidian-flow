package com.example.backend.repositories;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.backend.database.Database;
import com.example.backend.models.Project;
import com.example.backend.models.User;
import com.example.backend.util.Util;

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
            "INSERT INTO Projects (title, description, manager_id, created_date) VALUES (?, ?, ?, %s)",
            LocalDateTime.now().toString()
        );
    }

    public boolean isUserMember(Long projectId, Long userId)
    {
        try 
        {
            var result = dbService.executeQuery(
                "SELECT 1 FROM Project_Memberships WHERE project_id = ? AND project_member_id = ? LIMIT 1",
                projectId,
                userId
            );
            return result.next();

        } catch (Exception e) {
            return false;
        }
    }

    public boolean addMemberToProject(Long projectId, Long userId)
    {
        try 
        {
            dbService.executeUpdate(
                "INSERT INTO Project_Memberships (project_id, project_member_id, assignment_date) VALUES (?, ?, ?)",
                projectId,
                userId,
                LocalDateTime.now().toString()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<User> getProjectMembers(Long projectId)
    {
        try 
        {
            var result = dbService.executeQuery("""
                    SELECT u.* 
                    FROM Users u
                    JOIN Project_Memberships pm 
                    ON u.Id = pm.project_member_id
                    WHERE pm.project_id = ?
                """,
                projectId
            );
            return Util.MapResultToModelList(result, User.class);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
