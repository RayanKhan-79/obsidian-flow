package com.example.backend.services;

import java.util.Optional;

import com.example.backend.database.Database;
import com.example.backend.models.User;
import com.example.backend.repositories.UserRepo;

public class AuthService {

    private static AuthService instance;
    private final ActivityLogService logService;

    public static AuthService GetInstance() {
        if (instance == null)
            instance = new AuthService();

        return instance;
    }

    public Optional<User> currentUser;
    private UserRepo userRepo;

    private AuthService() {
        userRepo = new UserRepo(Database.GetInstance());
        logService = ActivityLogService.GetInstance();
    }

    public Boolean login(String email, String password) {
        currentUser = userRepo.FindByIdentifierAndPassword(email, password);

        if (currentUser.isPresent())
            logService.addLogMessage(String.format("%s has logged in", currentUser.get().email));

        return currentUser.isPresent();
    }

    public Boolean register(String firstName, String lastName, String email, String password) {
        userRepo.Create(firstName, lastName, email, password);

        return login(email, password);
    }

    public void logout() {
        if (currentUser.isPresent())
            logService.addLogMessage(String.format("%s has logged out", currentUser.get().email));

        currentUser = Optional.empty();
    }

}
