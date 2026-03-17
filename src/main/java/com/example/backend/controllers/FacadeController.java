package com.example.backend.controllers;

import com.example.backend.repositories.CommentRepo;
import com.example.backend.repositories.ProjectRepo;
import com.example.backend.repositories.TaskRepo;
import com.example.backend.repositories.UserRepo;

// Business Logic

public class FacadeController 
{
    ProjectRepo projectRepo;
    CommentRepo commentRepo;
    TaskRepo taskRepo;
    UserRepo userRepo;


    public void onCreateProject()
    {

    }

    public void onDeleteProject(Long projectId)
    {
        taskRepo.GetAllByProject(projectId).forEach((task) -> onDeleteTask(task.Id));
        projectRepo.Delete(projectId);
    }

    public void onDeleteTask(Long taskId)
    {
        commentRepo.GetAllByTask(taskId).forEach((comment) -> commentRepo.Delete(comment.Id));
        taskRepo.Delete(taskId);
    }
}
