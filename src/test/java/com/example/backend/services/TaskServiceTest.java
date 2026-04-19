package com.example.backend.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.example.backend.database.Database;
import com.example.backend.models.Project;
import com.example.backend.models.Task;
import com.example.backend.models.User;
import com.example.backend.repositories.ProjectRepo;
import com.example.backend.repositories.TaskRepo;

/**
 * Test class for TaskService
 *
 * Black-box test cases:
 * - Equivalence partitioning: valid/invalid projectId, title, etc.
 * - Boundary values: null values, empty strings
 * - Error guessing: user not authenticated, not member, project not exist
 *
 * White-box coverage:
 * - All branches in createTask method
 * - Preconditions checks
 */
public class TaskServiceTest {

    private TaskService taskService;
    private AuthService mockAuthService;
    private ProjectRepo mockProjectRepo;
    private TaskRepo mockTaskRepo;
    private ActivityLogService mockLogService;
    private MockedStatic<Database> mockDatabase;
    private MockedStatic<ActivityLogService> mockActivityLogService;
    private MockedStatic<AuthService> mockAuthServiceStatic;
    private MockedStatic<ProjectService> mockProjectServiceStatic; // If needed

    @BeforeEach
    void setUp() {
        // Mock singletons
        mockDatabase = mockStatic(Database.class);
        Database mockDb = mock(Database.class);
        mockDatabase.when(Database::GetInstance).thenReturn(mockDb);

        mockActivityLogService = mockStatic(ActivityLogService.class);
        mockLogService = mock(ActivityLogService.class);
        mockActivityLogService.when(ActivityLogService::GetInstance).thenReturn(mockLogService);

        mockAuthServiceStatic = mockStatic(AuthService.class);
        mockAuthService = mock(AuthService.class);
        mockAuthServiceStatic.when(AuthService::GetInstance).thenReturn(mockAuthService);

        // Mock repos
        mockProjectRepo = mock(ProjectRepo.class);
        mockTaskRepo = mock(TaskRepo.class);

        // Create TaskService instance using reflection
        try {
            var constructor = TaskService.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            taskService = constructor.newInstance();
            // Set fields
            var authField = TaskService.class.getDeclaredField("authService");
            authField.setAccessible(true);
            authField.set(taskService, mockAuthService);
            var projectRepoField = TaskService.class.getDeclaredField("projectRepo");
            projectRepoField.setAccessible(true);
            projectRepoField.set(taskService, mockProjectRepo);
            var taskRepoField = TaskService.class.getDeclaredField("taskRepo");
            taskRepoField.setAccessible(true);
            taskRepoField.set(taskService, mockTaskRepo);
            var logField = TaskService.class.getDeclaredField("logService");
            logField.setAccessible(true);
            logField.set(taskService, mockLogService);
        } catch (Exception e) {
            fail("Failed to create TaskService instance");
        }
    }

    @AfterEach
    void tearDown() {
        mockDatabase.close();
        mockActivityLogService.close();
        mockAuthServiceStatic.close();
    }

    @Test
    void testCreateTask_ValidData_ShouldReturnTask() {
        // Setup mocks
        Long projectId = 1L;
        String title = "Test Task";
        String description = "Description";
        Long priority = 1L;
        LocalDateTime dueDate = LocalDateTime.now();

        User mockUser = mock(User.class);
        when(mockUser.Id).thenReturn(1L);
        when(mockUser.email).thenReturn("user@example.com");
        when(mockAuthService.currentUser).thenReturn(Optional.of(mockUser));

        Project mockProject = mock(Project.class);
        when(mockProject.manager_id).thenReturn(1L);
        when(mockProject.title).thenReturn("Test Project");
        when(mockProjectRepo.Find(projectId)).thenReturn(Optional.of(mockProject));
        when(mockProjectRepo.isUserMember(projectId, 1L)).thenReturn(true);

        Task mockTask = mock(Task.class);
        when(mockTask.title).thenReturn(title);
        when(mockTaskRepo.Create(any(), any(), any(), any(), any(), any())).thenReturn(Optional.of(mockTask));

        Optional<Task> result = taskService.createTask(projectId, title, description, priority, dueDate);

        assertTrue(result.isPresent());
        verify(mockLogService).addLogMessage(anyString());
    }

    @Test
    void testCreateTask_NullProjectId_ShouldReturnEmpty() {
        Optional<Task> result = taskService.createTask(null, "Title", "Desc", 1L, null);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateTask_NullTitle_ShouldReturnEmpty() {
        Optional<Task> result = taskService.createTask(1L, null, "Desc", 1L, null);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateTask_EmptyTitle_ShouldReturnEmpty() {
        Optional<Task> result = taskService.createTask(1L, "", "Desc", 1L, null);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateTask_WhitespaceTitle_ShouldReturnEmpty() {
        Optional<Task> result = taskService.createTask(1L, "   ", "Desc", 1L, null);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateTask_UserNotAuthenticated_ShouldReturnEmpty() {
        when(mockAuthService.currentUser).thenReturn(Optional.empty());
        Optional<Task> result = taskService.createTask(1L, "Title", "Desc", 1L, null);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateTask_ProjectNotExist_ShouldReturnEmpty() {
        User mockUser = mock(User.class);
        when(mockAuthService.currentUser).thenReturn(Optional.of(mockUser));
        when(mockProjectRepo.Find(1L)).thenReturn(Optional.empty());

        Optional<Task> result = taskService.createTask(1L, "Title", "Desc", 1L, null);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateTask_UserNotMember_ShouldReturnEmpty() {
        User mockUser = mock(User.class);
        when(mockUser.Id).thenReturn(2L);
        when(mockAuthService.currentUser).thenReturn(Optional.of(mockUser));

        Project mockProject = mock(Project.class);
        when(mockProject.manager_id).thenReturn(1L);
        when(mockProjectRepo.Find(1L)).thenReturn(Optional.of(mockProject));
        when(mockProjectRepo.isUserMember(1L, 2L)).thenReturn(false);

        Optional<Task> result = taskService.createTask(1L, "Title", "Desc", 1L, null);
        assertTrue(result.isEmpty());
    }
}