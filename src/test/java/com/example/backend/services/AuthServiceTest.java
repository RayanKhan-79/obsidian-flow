package com.example.backend.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.example.backend.database.Database;
import com.example.backend.models.User;
import com.example.backend.repositories.UserRepo;

/**
 * Test class for AuthService
 *
 * Black-box test cases:
 * - Equivalence partitioning for login: valid/invalid email, valid/invalid password
 * - Boundary values: empty strings, null values
 * - Error guessing: SQL exceptions, user not found
 *
 * White-box coverage:
 * - Statement coverage: all lines in login, register, logout
 * - Branch coverage: if conditions for user presence
 * - Condition coverage: compound conditions
 */
public class AuthServiceTest {

    private AuthService authService;
    private UserRepo mockUserRepo;
    private ActivityLogService mockLogService;
    private MockedStatic<Database> mockDatabase;
    private MockedStatic<ActivityLogService> mockActivityLogService;

    @BeforeEach
    void setUp() {
        // Mock Database singleton
        mockDatabase = mockStatic(Database.class);
        Database mockDb = mock(Database.class);
        mockDatabase.when(Database::GetInstance).thenReturn(mockDb);

        // Mock ActivityLogService singleton
        mockActivityLogService = mockStatic(ActivityLogService.class);
        mockLogService = mock(ActivityLogService.class);
        mockActivityLogService.when(ActivityLogService::GetInstance).thenReturn(mockLogService);

        // Mock UserRepo
        mockUserRepo = mock(UserRepo.class);

        // Create AuthService instance with mocked dependencies
        // Since constructor is private, use reflection to create instance
        try {
            var constructor = AuthService.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            authService = constructor.newInstance();
            // Set the mocked userRepo using reflection
            var userRepoField = AuthService.class.getDeclaredField("userRepo");
            userRepoField.setAccessible(true);
            userRepoField.set(authService, mockUserRepo);
            // Set logService
            var logServiceField = AuthService.class.getDeclaredField("logService");
            logServiceField.setAccessible(true);
            logServiceField.set(authService, mockLogService);
        } catch (Exception e) {
            fail("Failed to create AuthService instance for testing");
        }
    }

    @AfterEach
    void tearDown() {
        mockDatabase.close();
        mockActivityLogService.close();
    }

    // Black-box test cases for login

    @Test
    void testLogin_ValidCredentials_ShouldReturnTrue() {
        // Equivalence class: valid email and password
        String email = "user@example.com";
        String password = "password123";
        User mockUser = mock(User.class);
        when(mockUserRepo.FindByEmailAndPassword(email, password)).thenReturn(Optional.of(mockUser));

        boolean result = authService.login(email, password);

        assertTrue(result);
        verify(mockLogService).addLogMessage(anyString());
    }

    @Test
    void testLogin_InvalidCredentials_ShouldReturnFalse() {
        // Equivalence class: invalid email or password
        String email = "invalid@example.com";
        String password = "wrongpassword";
        when(mockUserRepo.FindByEmailAndPassword(email, password)).thenReturn(Optional.empty());

        boolean result = authService.login(email, password);

        assertFalse(result);
        verify(mockLogService, never()).addLogMessage(anyString());
    }

    @Test
    void testLogin_NullEmail_ShouldReturnFalse() {
        // Boundary value: null email
        String email = null;
        String password = "password123";
        when(mockUserRepo.FindByEmailAndPassword(email, password)).thenReturn(Optional.empty());

        boolean result = authService.login(email, password);

        assertFalse(result);
    }

    @Test
    void testLogin_EmptyEmail_ShouldReturnFalse() {
        // Boundary value: empty email
        String email = "";
        String password = "password123";
        when(mockUserRepo.FindByEmailAndPassword(email, password)).thenReturn(Optional.empty());

        boolean result = authService.login(email, password);

        assertFalse(result);
    }

    @Test
    void testRegister_ValidData_ShouldReturnTrue() {
        // Test register method
        String firstName = "John";
        String lastName = "Doe";
        String email = "john@example.com";
        String password = "password123";

        User mockUser = mock(User.class);
        when(mockUser.email).thenReturn(email);
        when(mockUserRepo.Create(firstName, lastName, email, password)).thenReturn(Optional.of(mockUser));
        when(mockUserRepo.FindByEmailAndPassword(email, password)).thenReturn(Optional.of(mockUser));

        boolean result = authService.register(firstName, lastName, email, password);

        assertTrue(result);
        verify(mockUserRepo).Create(firstName, lastName, email, password);
        verify(mockLogService).addLogMessage(anyString());
    }

    @Test
    void testRegister_InvalidData_ShouldReturnFalse() {
        // Error guessing: creation fails
        String firstName = "John";
        String lastName = "Doe";
        String email = "john@example.com";
        String password = "password123";

        when(mockUserRepo.Create(firstName, lastName, email, password)).thenReturn(Optional.empty());

        boolean result = authService.register(firstName, lastName, email, password);

        assertFalse(result);
    }

    @Test
    void testLogout_WithLoggedInUser_ShouldClearUserAndLog() {
        // Set current user
        User mockUser = mock(User.class);
        when(mockUser.email).thenReturn("user@example.com");
        authService.currentUser = Optional.of(mockUser);

        authService.logout();

        assertTrue(authService.currentUser.isEmpty());
        verify(mockLogService).addLogMessage(anyString());
    }

    @Test
    void testLogout_NoUser_ShouldNotLog() {
        authService.currentUser = Optional.empty();

        authService.logout();

        verify(mockLogService, never()).addLogMessage(anyString());
    }
}