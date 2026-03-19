package com.example.backend.repositories;

import java.sql.ResultSet;
import java.util.Optional;

import com.example.backend.database.Database;
import com.example.backend.models.User;
import com.example.backend.util.Util;

public class UserRepo extends RepositoryBase<User> {

    public UserRepo(Database dbService) 
    {
        super("Users", dbService,User.class);
    }

    @Override
    protected String InsertQuery() {
        return String.format(
            "INSERT INTO %s (first_name, last_name, email, password, user_type) VALUES (?, ?, ?, ?, ?, ?)",
            tableName
        );
    }
    
    public Optional<User> FindByEmailAndPassword(String email, String password)
    {
        ResultSet result =  dbService.executeQuery("SELECT * FROM %s WHERE email = ? AND password = ?", email, password);
        return Util.MapResultToModel(result, model);
    }
}
