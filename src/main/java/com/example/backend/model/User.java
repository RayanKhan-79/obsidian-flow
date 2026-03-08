package com.example.backend.model;

import java.sql.ResultSet;
import java.sql.SQLException;

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
            Id = rs.getLong("Id");
            fname = rs.getString("first_name");
            lname = rs.getString("last_name");
            email = rs.getString("email");
            password = rs.getString("password");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
