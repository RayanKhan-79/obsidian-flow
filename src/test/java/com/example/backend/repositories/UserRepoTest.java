package com.example.backend.repositories;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.example.backend.database.Database;
import com.example.backend.enums.Permissions;
import com.example.backend.models.User;

/**
 * Test class for UserRepo
 *
 * Black-box test cases:
 * - Equivalence partitioning: valid/invalid emails, passwords
 * - Boundary values: null, empty
 *
 * White-box coverage:
 * - All methods in UserRepo
 */
public class UserRepoTest {

    private UserRepo userRepo;
    private Database mockDatabase;
    private MockedStatic<Database> mockDatabaseStatic;

    @BeforeEach
    void setUp() {
        mockDatabaseStatic = mockStatic(Database.class);
        mockDatabase = mock(Database.class);
        mockDatabaseStatic.when(Database::GetInstance).thenReturn(mockDatabase);

        userRepo = new UserRepo(mockDatabase);
    }

    @AfterEach
    void tearDown() {
        mockDatabaseStatic.close();
    }

    @Test
    void testFindByEmailAndPassword_ValidCredentials_ShouldReturnUser() throws SQLException {
        String email = "user@example.com";
        String password = "password123";

        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong("id")).thenReturn(1L);
        when(mockResultSet.getString("first_name")).thenReturn("John");
        when(mockResultSet.getString("last_name")).thenReturn("Doe");
        when(mockResultSet.getString("email")).thenReturn(email);
        when(mockResultSet.getString("password")).thenReturn(password);

        when(mockDatabase.executeQuery(anyString(), eq(email), eq(password))).thenReturn(mockResultSet);

        Optional<User> result = userRepo.FindByEmailAndPassword(email, password);

        assertTrue(result.isPresent());
        assertEquals("John", result.get().fname);
    }

    @Test
    void testFindByEmailAndPassword_InvalidCredentials_ShouldReturnEmpty() throws SQLException {
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockResultSet.next()).thenReturn(false);

        when(mockDatabase.executeQuery(anyString(), any(), any())).thenReturn(mockResultSet);

        Optional<User> result = userRepo.FindByEmailAndPassword("invalid", "wrong");

        assertTrue(result.isEmpty());
    }

    @Test
    void testAddPermission_Success_ShouldReturnTrue() throws SQLException {
        when(mockDatabase.executeUpdate(anyString(), any(), any())).thenReturn(1);

        boolean result = userRepo.AddPermission(1L, Permissions.PROJECT_MANAGER);

        assertTrue(result);
    }

    @Test
    void testAddPermission_Failure_ShouldReturnFalse() throws SQLException {
        when(mockDatabase.executeUpdate(anyString(), any(), any())).thenThrow(SQLException.class);

        boolean result = userRepo.AddPermission(1L, Permissions.PROJECT_MANAGER);

        assertFalse(result);
    }

    @Test
    void testHasPermission_True_ShouldReturnTrue() throws SQLException {
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockResultSet.next()).thenReturn(true);

        when(mockDatabase.executeQuery(anyString(), any(), any())).thenReturn(mockResultSet);

        boolean result = userRepo.HasPermission(1L, Permissions.PROJECT_MANAGER);

        assertTrue(result);
    }

    @Test
    void testHasPermission_False_ShouldReturnFalse() throws SQLException {
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockResultSet.next()).thenReturn(false);

        when(mockDatabase.executeQuery(anyString(), any(), any())).thenReturn(mockResultSet);

        boolean result = userRepo.HasPermission(1L, Permissions.PROJECT_MANAGER);

        assertFalse(result);
    }
}