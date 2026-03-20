package com.example.backend.services;

import java.util.Optional;

import com.example.backend.database.Database;
import com.example.backend.enums.UserTypes;
import com.example.backend.models.User;
import com.example.backend.repositories.ProjectManagerRepo;
import com.example.backend.repositories.ProjectMemberRepo;
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
    private ProjectManagerRepo projectManagerRepo;
    private ProjectMemberRepo ProjectMemberRepo;


    private AuthService() {
        userRepo = new UserRepo(Database.GetInstance());
        projectManagerRepo = new ProjectManagerRepo(Database.GetInstance());
        ProjectMemberRepo = new ProjectMemberRepo(Database.GetInstance());
    }



    public Boolean login(String email, String password)
    {
        currentUser = userRepo.FindByEmailAndPassword(email, password);
        return currentUser.isPresent();
    }
    
    public Boolean register(UserTypes userType, String firstName, String lastName, String email, String password)
    {
        userRepo.Create(firstName, lastName, email, password, userType.toString())
            .ifPresent((user) -> {
                switch (userType) 
                {
                    case UserTypes.PROJECT_MANAGER: 
                        projectManagerRepo.Create(user.Id); 
                        return;
                    case UserTypes.PROJECT_MEMBER: 
                        ProjectMemberRepo.Create(user.Id); 
                        return;
                }
            });


        return login(email, password);
    }

}
