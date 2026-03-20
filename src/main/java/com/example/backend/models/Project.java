package com.example.backend.models;

import java.sql.ResultSet;
import java.time.LocalDateTime;

import com.example.backend.database.Constants;

public class Project 
{
    public Long Id;
    public String title;
    public String description;
    public Long manager_id;
    public LocalDateTime createdDate;

    public Project(ResultSet rs)
    {
        try 
        {
            Id = rs.getLong(Constants.ID);
            title = rs.getString(Constants.TITLE);
            description = rs.getString(Constants.DESCRIPTION);
            manager_id = rs.getLong(Constants.MANAGER_ID);
            createdDate = LocalDateTime.parse(rs.getString(Constants.CREATED_DATE));
            
        } catch (Exception e) {
            System.out.println("Project Parsing Error");
            throw new RuntimeException();
        }
    }
}
