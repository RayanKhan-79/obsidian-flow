package com.example.backend.models;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.backend.database.Constants;

public class User 
{
    public Long Id;
    public String fname;
    public String lname;
    public String email;
    public String password;

    public User(ResultSet rs)
    {
        try 
        {
            Id = rs.getLong(Constants.ID);
            fname = rs.getString(Constants.FIRST_NAME);
            lname = rs.getString(Constants.LAST_NAME);
            email = rs.getString(Constants.EMAIL);
            password = rs.getString(Constants.PASSWORD);
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to parse User", e);
        }
    }
}
