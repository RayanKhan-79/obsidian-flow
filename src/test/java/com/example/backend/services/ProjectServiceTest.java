package com.example.backend.services;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.backend.database.Database;
import com.example.backend.models.Project;
import com.example.backend.models.User;
import com.example.backend.repositories.UserRepo;
import com.example.backend.testsupport.TestDatabaseHelper;

public class ProjectServiceTest {

    private File dbFile;
    private AuthService authService;
    private ProjectService projectService;
    private UserRepo userRepo;

    @BeforeEach
    void setUp() {
        dbFile = TestDatabaseHelper.createTestDatabaseFile();
        TestDatabaseHelper.initializeDatabase(dbFile);
        authService = AuthService.GetInstance();
        projectService = ProjectService.GetInstance();
        userRepo = new UserRepo(Database.GetInstance());
    }

    @AfterEach
    void tearDown() {
        TestDatabaseHelper.cleanupDatabaseFile(dbFile);
    }

    @Test
    void testCreateProjectAsManagerShouldPersistProject() {
        assertTrue(authService.login("admin@example.com", "admin123"));

        Optional<Project> project = projectService.createProject("Test Project", "Description");

        assertTrue(project.isPresent());
        assertEquals("Test Project", project.get().title);
        assertNotNull(project.get().Id);
    }

    @Test
    void testCreateProjectWithEmptyTitleShouldReturnEmpty() {
        assertTrue(authService.login("admin@example.com", "admin123"));

        Optional<Project> project = projectService.createProject("   ", "Description");

        assertTrue(project.isEmpty());
    }

    @Test
    void testAddMemberToProjectShouldPersistMembership() {
        assertTrue(authService.login("admin@example.com", "admin123"));
        Project project = projectService.createProject("Test Project", "Description").orElseThrow();

        String memberEmail = String.format("member-%s@example.com", UUID.randomUUID());
        User member = userRepo.Create("Member", "User", memberEmail, "pass123").orElseThrow();

        assertTrue(projectService.addMemberToProject(project.Id, member.Id));
        assertTrue(projectService.isUserInProject(project.Id, member.Id));
    }

    @Test
    void testAddMemberToProjectWhenNotManagerShouldReturnFalse() {
        assertTrue(authService.login("admin@example.com", "admin123"));
        Project project = projectService.createProject("Test Project", "Description").orElseThrow();

        String memberEmail = String.format("member-%s@example.com", UUID.randomUUID());
        User member = userRepo.Create("Member", "User", memberEmail, "pass123").orElseThrow();

        authService.logout();
        assertTrue(authService.register("Guest", "User", "guest-" + UUID.randomUUID() + "@example.com", "pass123"));

        assertFalse(projectService.addMemberToProject(project.Id, member.Id));
    }
}
