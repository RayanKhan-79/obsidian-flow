package com.example.backend.services;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.backend.testsupport.TestDatabaseHelper;

public class AuthServiceTest {

    private File dbFile;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        dbFile = TestDatabaseHelper.createTestDatabaseFile();
        TestDatabaseHelper.initializeDatabase(dbFile);
        authService = AuthService.GetInstance();
    }

    @AfterEach
    void tearDown() {
        TestDatabaseHelper.cleanupDatabaseFile(dbFile);
    }

    @Test
    void testRegisterLoginLogout() {
        String email = String.format("user-%s@example.com", UUID.randomUUID());
        String password = "Password123";

        assertTrue(authService.register("John", "Doe", email, password));
        assertTrue(authService.currentUser.isPresent());
        assertEquals(email, authService.currentUser.get().email);

        authService.logout();
        assertTrue(authService.currentUser.isEmpty());

        assertTrue(authService.login(email, password));
        assertTrue(authService.currentUser.isPresent());
        assertEquals(email, authService.currentUser.get().email);
    }

    @Test
    void testLoginWithEmailPrefix() {
        String uuidPart = UUID.randomUUID().toString().replace("-", "");
        String email = String.format("member-%s@example.com", uuidPart);
        String password = "Password123";

        assertTrue(authService.register("Jane", "Smith", email, password));
        authService.logout();

        String identifier = email.substring(0, email.indexOf('@'));
        assertTrue(authService.login(identifier, password));
        assertTrue(authService.currentUser.isPresent());
        assertEquals(email, authService.currentUser.get().email);
    }

    @Test
    void testLoginInvalidCredentials() {
        assertFalse(authService.login("doesnotexist@example.com", "wrongpass"));
        assertTrue(authService.currentUser.isEmpty());
    }

    @Test
    void testDuplicateRegisterReturnsFalse() {
        String email = String.format("duplicate-%s@example.com", UUID.randomUUID());
        String password = "Password123";

        assertTrue(authService.register("Alice", "White", email, password));
        authService.logout();

        Boolean success = authService.register("Alice", "White", email, password);
        assertFalse(success);
    }

    @Test
    void testLogoutWhenNoUserIsLoggedIn() {
        authService.currentUser = Optional.empty();
        authService.logout();
        assertTrue(authService.currentUser.isEmpty());
    }
}
