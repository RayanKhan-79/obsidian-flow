package com.example.backend.models;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.backend.database.Constants;

public class ProjectMember extends User
{
    Long userId;

    public ProjectMember(ResultSet rs) 
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
