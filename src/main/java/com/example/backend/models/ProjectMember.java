package com.example.backend.models;

import java.sql.ResultSet;

public class ProjectMember extends User
{

    public ProjectMember(ResultSet rs) 
    {
        super(rs);
    }

    public ProjectMember(Long id, String fname, String lname, String email, String password) 
    {
        super(id, fname, lname, email, password);
    }
        
}
