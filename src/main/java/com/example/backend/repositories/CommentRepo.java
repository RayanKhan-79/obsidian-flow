package com.example.backend.repositories;

import java.util.List;

import com.example.backend.database.Constants;
import com.example.backend.database.Database;
import com.example.backend.models.Comment;
import com.example.backend.util.Util;

public class CommentRepo extends RepositoryBase<Comment> {

    public CommentRepo(Database dbService) 
    {
        super("comments", dbService, Comment.class);
    }

    @Override
    protected String InsertQuery() {
        return String.format(
            "INSERT INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, ?)", 
            tableName, 
            Constants.TASK_ID, 
            Constants.USER_ID, 
            Constants.TEXT, 
            Constants.CREATED_DATE
        );
    }
    
    public List<Comment> GetAllByTask(Long taskId)
    {
        var result = dbService.executeQuery(
            String.format("SELECT * FROM %s WHERE %s = ?", tableName, Constants.TASK_ID), taskId
        ); 
        
        return Util.MapResultToModelList(result, Comment.class);
    }
}
