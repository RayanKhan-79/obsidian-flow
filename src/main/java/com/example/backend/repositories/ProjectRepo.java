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
    public ProjectRepo(Database dbService) 
    {
        super("projects", dbService, Project.class);
    }

    @Override
    protected String InsertQuery() 
    {
        return String.format(
            "INSERT INTO projects (title, description, manager_id, created_date) VALUES (?, ?, ?, '%s')",
            LocalDateTime.now().toString()
        );
    }

    public List<Project> GetAllForUser(Long userId)
    {
        try
        {
            var result = dbService.executeQuery(
                """
                    SELECT DISTINCT p.*
                    FROM projects p
                    LEFT JOIN project_memberships pm ON p.id = pm.project_id
                    WHERE p.manager_id = ? OR pm.project_member_id = ?
                    ORDER BY p.id DESC
                """,
                userId,
                userId
            );
            return Util.MapResultToModelList(result, Project.class);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public boolean isUserMember(Long projectId, Long userId)
    {
        try 
        {
            var result = dbService.executeQuery(
                "SELECT 1 FROM project_memberships WHERE project_id = ? AND project_member_id = ? LIMIT 1",
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
                "INSERT INTO project_memberships (project_id, project_member_id, assignment_date) VALUES (?, ?, ?)",
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
                    FROM users u
                    JOIN project_memberships pm 
                    ON u.id = pm.project_member_id
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
