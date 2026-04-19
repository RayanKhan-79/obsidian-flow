package com.example.backend.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.example.backend.database.Database;
import com.example.backend.enums.Permissions;
import com.example.backend.models.Project;
import com.example.backend.models.User;
import com.example.backend.repositories.ProjectRepo;
import com.example.backend.repositories.TaskRepo;
import com.example.backend.repositories.UserRepo;

/**
 * Test class for ProjectService
 *
 * Black-box test cases:
 * - Equivalence partitioning for createProject: valid/invalid title, permissions
 * - Boundary values: null, empty strings
 *
 * White-box coverage:
 * - All branches in createProject, addMemberToProject, etc.
 */
public class ProjectServiceTest {

    private ProjectService projectService;
    private AuthService mockAuthService;
    private ActivityLogService mockLogService;
    private ProjectRepo mockProjectRepo;
    private UserRepo mockUserRepo;
    private TaskRepo mockTaskRepo;
    private MockedStatic<Database> mockDatabase;
    private MockedStatic<ActivityLogService> mockActivityLogService;
    private MockedStatic<AuthService> mockAuthServiceStatic;

    @BeforeEach
    void setUp() {
        mockDatabase = mockStatic(Database.class);
        Database mockDb = mock(Database.class);
        mockDatabase.when(Database::GetInstance).thenReturn(mockDb);

        mockActivityLogService = mockStatic(ActivityLogService.class);
        mockLogService = mock(ActivityLogService.class);
        mockActivityLogService.when(ActivityLogService::GetInstance).thenReturn(mockLogService);

        mockAuthServiceStatic = mockStatic(AuthService.class);
        mockAuthService = mock(AuthService.class);
        mockAuthServiceStatic.when(AuthService::GetInstance).thenReturn(mockAuthService);

        mockProjectRepo = mock(ProjectRepo.class);
        mockUserRepo = mock(UserRepo.class);
        mockTaskRepo = mock(TaskRepo.class);

        try {
            var constructor = ProjectService.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            projectService = constructor.newInstance();
            var authField = ProjectService.class.getDeclaredField("authService");
            authField.setAccessible(true);
            authField.set(projectService, mockAuthService);
            var logField = ProjectService.class.getDeclaredField("logService");
            logField.setAccessible(true);
            logField.set(projectService, mockLogService);
            var projectRepoField = ProjectService.class.getDeclaredField("projectRepo");
            projectRepoField.setAccessible(true);
            projectRepoField.set(projectService, mockProjectRepo);
            var userRepoField = ProjectService.class.getDeclaredField("userRepo");
            userRepoField.setAccessible(true);
            userRepoField.set(projectService, mockUserRepo);
            var taskRepoField = ProjectService.class.getDeclaredField("taskRepo");
            taskRepoField.setAccessible(true);
            taskRepoField.set(projectService, mockTaskRepo);
        } catch (Exception e) {
            fail("Failed to create ProjectService instance");
        }
    }

    @AfterEach
    void tearDown() {
        mockDatabase.close();
        mockActivityLogService.close();
        mockAuthServiceStatic.close();
    }

    @Test
    void testCreateProject_ValidData_ShouldReturnProject() {
        String title = "Test Project";
        String description = "Description";

        User mockUser = mock(User.class);
        when(mockUser.Id).thenReturn(1L);
        when(mockUser.email).thenReturn("user@example.com");
        when(mockAuthService.currentUser).thenReturn(Optional.of(mockUser));
        when(mockUserRepo.HasPermission(1L, Permissions.PROJECT_MANAGER)).thenReturn(true);

        Project mockProject = mock(Project.class);
        when(mockProject.title).thenReturn(title);
        when(mockProjectRepo.Create(title, description, 1L)).thenReturn(Optional.of(mockProject));

        Optional<Project> result = projectService.createProject(title, description);

        assertTrue(result.isPresent());
        verify(mockLogService).addLogMessage(anyString());
    }

    @Test
    void testCreateProject_NoPermission_ShouldReturnEmpty() {
        User mockUser = mock(User.class);
        when(mockUser.Id).thenReturn(1L);
        when(mockAuthService.currentUser).thenReturn(Optional.of(mockUser));
        when(mockUserRepo.HasPermission(1L, Permissions.PROJECT_MANAGER)).thenReturn(false);

        Optional<Project> result = projectService.createProject("Title", "Desc");

        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateProject_NullTitle_ShouldReturnEmpty() {
        User mockUser = mock(User.class);
        when(mockAuthService.currentUser).thenReturn(Optional.of(mockUser));

        Optional<Project> result = projectService.createProject(null, "Desc");

        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateProject_EmptyTitle_ShouldReturnEmpty() {
        User mockUser = mock(User.class);
        when(mockAuthService.currentUser).thenReturn(Optional.of(mockUser));

        Optional<Project> result = projectService.createProject("", "Desc");

        assertTrue(result.isEmpty());
    }

    @Test
    void testAddMemberToProject_ValidData_ShouldReturnTrue() {
        Long projectId = 1L;
        Long memberId = 2L;

        User mockUser = mock(User.class);
        when(mockUser.Id).thenReturn(1L);
        when(mockUser.email).thenReturn("manager@example.com");
        when(mockAuthService.currentUser).thenReturn(Optional.of(mockUser));

        Project mockProject = mock(Project.class);
        when(mockProject.manager_id).thenReturn(1L);
        when(mockProject.title).thenReturn("Test Project");
        when(mockProjectRepo.Find(projectId)).thenReturn(Optional.of(mockProject));
        when(mockUserRepo.HasPermission(memberId, Permissions.PROJECT_MANAGER)).thenReturn(true);

        User mockMember = mock(User.class);
        when(mockMember.email).thenReturn("member@example.com");
        when(mockUserRepo.Find(memberId)).thenReturn(Optional.of(mockMember));
        when(mockProjectRepo.isUserMember(projectId, memberId)).thenReturn(false);
        when(mockProjectRepo.addMemberToProject(projectId, memberId)).thenReturn(true);

        boolean result = projectService.addMemberToProject(projectId, memberId);

        assertTrue(result);
        verify(mockLogService).addLogMessage(anyString());
    }

    @Test
    void testAddMemberToProject_NotManager_ShouldReturnFalse() {
        Long projectId = 1L;
        Long memberId = 2L;

        User mockUser = mock(User.class);
        when(mockUser.Id).thenReturn(2L); // Not manager
        when(mockAuthService.currentUser).thenReturn(Optional.of(mockUser));

        Project mockProject = mock(Project.class);
        when(mockProject.manager_id).thenReturn(1L);
        when(mockProjectRepo.Find(projectId)).thenReturn(Optional.of(mockProject));

        boolean result = projectService.addMemberToProject(projectId, memberId);

        assertFalse(result);
    }
}