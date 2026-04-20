package com.example.backend.services;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.backend.database.Database;
import com.example.backend.enums.TaskStatus;
import com.example.backend.models.Project;
import com.example.backend.models.Task;
import com.example.backend.repositories.TaskRepo;
import com.example.backend.repositories.UserRepo;
import com.example.backend.testsupport.TestDatabaseHelper;

public class TaskServiceTest {

    private File dbFile;
    private AuthService authService;
    private ProjectService projectService;
    private TaskService taskService;
    private TaskRepo taskRepo;
    private UserRepo userRepo;

    @BeforeEach
    void setUp() {
        dbFile = TestDatabaseHelper.createTestDatabaseFile();
        TestDatabaseHelper.initializeDatabase(dbFile);
        authService = AuthService.GetInstance();
        projectService = ProjectService.GetInstance();
        taskService = TaskService.GetInstance();
        taskRepo = new TaskRepo(Database.GetInstance());
        userRepo = new UserRepo(Database.GetInstance());
    }

    @AfterEach
    void tearDown() {
        TestDatabaseHelper.cleanupDatabaseFile(dbFile);
    }

    @Test
    void testCreateTaskAsProjectManagerPersistsTask() {
        assertTrue(authService.login("admin@example.com", "admin123"));

        Project project = projectService.createProject("Project A", "Description").orElseThrow();
        Optional<Task> createdTask = taskService.createTask(project.Id, "Task 1", "Do work", null, 1L, LocalDateTime.now().plusDays(3));

        assertTrue(createdTask.isPresent());
        assertEquals("Task 1", createdTask.get().title);
        assertEquals(TaskStatus.PENDING, createdTask.get().status);
    }

    @Test
    void testCreateTaskByNonProjectMemberReturnsEmpty() {
        assertTrue(authService.login("admin@example.com", "admin123"));
        Project project = projectService.createProject("Project B", "Description").orElseThrow();

        String email = String.format("outsider-%s@example.com", UUID.randomUUID());
        userRepo.Create("Outsider", "User", email, "pass123");
        authService.logout();
        assertTrue(authService.login(email, "pass123"));

        Optional<Task> createdTask = taskService.createTask(project.Id, "Task 2", "Unauthorized task", null, 1L, null);

        assertTrue(createdTask.isEmpty());
    }

    @Test
    void testUpdateTaskStatusShouldPersistCompletedState() {
        assertTrue(authService.login("admin@example.com", "admin123"));
        Project project = projectService.createProject("Project C", "Description").orElseThrow();
        Task task = taskService.createTask(project.Id, "Task 3", "Update status", null, 2L, null).orElseThrow();

        assertTrue(taskService.updateTask(task.Id, null, null, "Done", null, LocalDateTime.now().plusDays(1)));

        Task updated = taskRepo.Find(task.Id).orElseThrow();
        assertEquals(TaskStatus.COMPLETED, updated.status);
    }
}
