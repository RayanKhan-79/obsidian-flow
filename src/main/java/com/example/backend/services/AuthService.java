package com.example.backend.services;

import java.util.Optional;

import com.example.backend.database.Database;
import com.example.backend.models.User;
import com.example.backend.repositories.UserRepo;

public class AuthService 
{
    
    private static AuthService instance;

    public static AuthService GetInstance()
    {
        if (instance == null)
            instance = new AuthService();

        return instance;
    }
    
    public Optional<User> currentUser;
    private UserRepo userRepo;


    private AuthService() {
        userRepo = new UserRepo(Database.GetInstance());
    }



    public Boolean login(String email, String password)
    {
        currentUser = userRepo.FindByEmailAndPassword(email, password);
        return currentUser.isPresent();
    }
    
    public Boolean register(String firstName, String lastName, String email, String password)
    {
        userRepo.Create(firstName, lastName, email, password);

        return login(email, password);
    }

}
