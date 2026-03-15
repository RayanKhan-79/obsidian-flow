package com.example.backend.models;

import java.sql.ResultSet;

public class ProjectManager extends User {

    public ProjectManager(Long id, String fname, String lname, String email, String password) 
    {
        super(id, fname, lname, email, password);
    }

    public ProjectManager(ResultSet rs)
    {
        super(rs);
    }
    
}
