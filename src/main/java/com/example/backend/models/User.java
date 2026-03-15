package com.example.backend.models;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.backend.services.database.Constants;

public class User 
{
    public Long Id;
    public String fname;
    public String lname;
    public String email;
    public String password;

    public User(Long id, String fname, String lname, String email, String password) 
    {
        Id = id;
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.password = password;
    }

    public User(ResultSet rs)
    {
        try 
        {
            Id = rs.getLong(Constants.Id);
            fname = rs.getString(Constants.First_Name);
            lname = rs.getString(Constants.LAST_Name);
            email = rs.getString(Constants.Email);
            password = rs.getString(Constants.Password);

        } catch (SQLException e) {
            System.out.println("User Parsing Error");
            System.exit(1);
        }
    }
}
