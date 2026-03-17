package com.example.backend.models;

import java.sql.ResultSet;
import java.time.LocalDateTime;

import com.example.backend.services.database.Constants;

public class Comment 
{
    public Long Id;
    public Long taskId;
    public Long userId;
    public String text;
    public LocalDateTime createdDate;

    public Comment(ResultSet rs)
    {
        try 
        {
            Id = rs.getLong(Constants.ID);
            taskId = rs.getLong(Constants.TASK_ID);
            userId = rs.getLong(Constants.USER_ID);
            text = rs.getString(Constants.TEXT);
            createdDate = LocalDateTime.parse(rs.getString(Constants.CREATED_DATE));
               
        } catch (Exception e) {
            System.out.println("Comment Parsing Error");
            throw new RuntimeException();
        }
    }
}
