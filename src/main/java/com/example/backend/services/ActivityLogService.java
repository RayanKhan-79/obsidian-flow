package com.example.backend.services;

import com.example.backend.database.Database;
import com.example.backend.repositories.ActivityLogRepo;

public class ActivityLogService {

    private static ActivityLogService instance;
    private final ActivityLogRepo activityLogRepo;

    public static ActivityLogService GetInstance() {
        if (instance == null)
            instance = new ActivityLogService();
        return instance;
    }

    private ActivityLogService() {
        activityLogRepo = new ActivityLogRepo(Database.GetInstance());
    }

    public void addLogMessage(String message) {
        activityLogRepo.Create(message);
    }
}
