package com.example.backend.services.auth;

import java.util.Optional;

import com.example.backend.model.User;
import com.example.backend.services.database.DBService;

public class AuthService 
{
    
    private static AuthService instance;
    
    public static AuthService GetInstance()
    {
        if (instance == null)
            instance = new AuthService();

        return instance;
    }
    
    private AuthService() {}

    Optional<User> currentUser;

    public Boolean login(String email, String password)
    {
        currentUser = DBService.GetInstance().FindUserByEmailAndPassword(email, password);
        return currentUser.isPresent();
    }
    
    public Boolean register(String firstName, String lastName, String email, String password)
    {
        DBService.GetInstance().CreateUser(firstName, lastName, email, password);
        return login(email, password);
    }

}
