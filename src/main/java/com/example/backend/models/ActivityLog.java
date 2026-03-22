package com.example.backend.models;

import java.sql.ResultSet;
import java.time.LocalDateTime;

import com.example.backend.database.Constants;

public class ActivityLog 
{
    public Long Id;
    public Long userId;
    public String description;
    public LocalDateTime timestamp;
    
    public ActivityLog(ResultSet resultSet)
    {
        try 
        {
            Id = resultSet.getLong(Constants.ID);
            userId = resultSet.getLong(Constants.USER_ID);
            description = resultSet.getString(Constants.DESCRIPTION);
            timestamp = LocalDateTime.parse(resultSet.getString(Constants.TIMESTAMP));

        } catch (Exception e) {
            System.out.println("Log Parsing Error");
        }
    }
}
