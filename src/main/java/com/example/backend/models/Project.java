package com.example.backend.models;

import java.sql.ResultSet;
import java.time.LocalDateTime;

import com.example.backend.services.database.Constants;

public class Project 
{
    public Long Id;
    public String title;
    public Long manager_id;
    public LocalDateTime createdDate;

    public Project(ResultSet rs)
    {
        try 
        {
            Id = rs.getLong(Constants.ID);
            title = rs.getString(Constants.Title);
            manager_id = rs.getLong(Constants.Manager_Id);
            createdDate = LocalDateTime.parse(rs.getString(Constants.Created_Date));
            
        } catch (Exception e) {
            System.out.println("Project Parsing Error");
            throw new RuntimeException();
        }
    }
}
