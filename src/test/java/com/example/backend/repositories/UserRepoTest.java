package com.example.backend.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.backend.database.Database;
import com.example.backend.enums.Permissions;
import com.example.backend.models.User;
import com.example.backend.testsupport.TestDatabaseHelper;

public class UserRepoTest {

    private File dbFile;
    private UserRepo userRepo;

    @BeforeEach
    void setUp() {
        dbFile = TestDatabaseHelper.createTestDatabaseFile();
        TestDatabaseHelper.initializeDatabase(dbFile);
        userRepo = new UserRepo(Database.GetInstance());
    }

    @AfterEach
    void tearDown() {
        TestDatabaseHelper.cleanupDatabaseFile(dbFile);
    }

    @Test
    void testCreateAndFindByEmailAndPassword() {
        String email = String.format("user-%s@example.com", UUID.randomUUID());
        String password = "Password123";

        User created = userRepo.Create("John", "Doe", email, password).orElseThrow();
        Optional<User> found = userRepo.FindByEmailAndPassword(email, password);

        assertTrue(found.isPresent());
        assertEquals(created.Id, found.get().Id);
        assertEquals(email, found.get().email);
    }

    @Test
    void testAddAndRemovePermissionUpdatesDatabase() {
        String email = String.format("perm-%s@example.com", UUID.randomUUID());
        User user = userRepo.Create("Perm", "User", email, "password").orElseThrow();

        assertFalse(userRepo.HasPermission(user.Id, Permissions.PROJECT_MANAGER));
        assertTrue(userRepo.AddPermission(user.Id, Permissions.PROJECT_MANAGER));
        assertTrue(userRepo.HasPermission(user.Id, Permissions.PROJECT_MANAGER));
        assertTrue(userRepo.RemovePermission(user.Id, Permissions.PROJECT_MANAGER));
        assertFalse(userRepo.HasPermission(user.Id, Permissions.PROJECT_MANAGER));
    }

    @Test
    void testExistsByEmailAndGetAllReturnsCreatedUser() {
        String email = String.format("exists-%s@example.com", UUID.randomUUID());
        userRepo.Create("Exists", "User", email, "password").orElseThrow();

        assertTrue(userRepo.ExistsByEmail(email));
        assertFalse(userRepo.ExistsByEmail("missing@example.com"));

        List<User> users = userRepo.GetAll();
        assertFalse(users.isEmpty());
        assertTrue(users.stream().anyMatch(u -> email.equals(u.email)));
    }
}
