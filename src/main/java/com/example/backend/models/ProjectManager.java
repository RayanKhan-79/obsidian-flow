package com.example.backend.models;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.backend.services.database.Constants;

public class ProjectManager extends User {

    public Long userId;
    
    public ProjectManager(ResultSet rs)
    {
        super(rs);
        try 
        {
            userId = rs.getLong(Constants.USER_ID);
        } catch (SQLException e) {
            System.out.println("Project Member Parsing Error");
            throw new RuntimeException();
        }
    }
}
