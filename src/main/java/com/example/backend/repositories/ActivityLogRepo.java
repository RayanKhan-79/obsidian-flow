package com.example.backend.repositories;

import java.time.LocalDateTime;

import com.example.backend.database.Constants;
import com.example.backend.database.Database;
import com.example.backend.models.ActivityLog;

public class ActivityLogRepo extends RepositoryBase<ActivityLog>
{
    public ActivityLogRepo(Database dbService) 
    {
        super("activity_log", dbService, ActivityLog.class);
    }

    @Override
    protected String InsertQuery() 
    {
        return String.format("INSERT INTO %s (%s, %s, %s) VALUES (?, ?, %s)",
            tableName, 
            Constants.USER_ID,
            Constants.DESCRIPTION, 
            Constants.TIMESTAMP,
            LocalDateTime.now().toString()
        );
    }
    
}
