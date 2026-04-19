package com.example.backend.models;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import com.example.backend.database.Constants;

/**
 * Test class for User model
 *
 * Black-box test cases:
 * - Equivalence partitioning: valid data, invalid data
 * - Error guessing: SQL exceptions
 *
 * White-box coverage:
 * - Constructor branches
 */
public class UserTest {

    @Test
    void testConstructor_ValidData_ShouldParseCorrectly() throws SQLException {
        ResultSet mockRs = mock(ResultSet.class);
        when(mockRs.getLong(Constants.ID)).thenReturn(1L);
        when(mockRs.getString(Constants.FIRST_NAME)).thenReturn("John");
        when(mockRs.getString(Constants.LAST_NAME)).thenReturn("Doe");
        when(mockRs.getString(Constants.EMAIL)).thenReturn("john@example.com");
        when(mockRs.getString(Constants.PASSWORD)).thenReturn("password123");

        User user = new User(mockRs);

        assertEquals(1L, user.Id);
        assertEquals("John", user.fname);
        assertEquals("Doe", user.lname);
        assertEquals("john@example.com", user.email);
        assertEquals("password123", user.password);
    }

    @Test
    void testConstructor_SQLException_ShouldThrowRuntimeException() throws SQLException {
        ResultSet mockRs = mock(ResultSet.class);
        when(mockRs.getLong(anyString())).thenThrow(SQLException.class);

        assertThrows(RuntimeException.class, () -> new User(mockRs));
    }

    @Test
    void testConstructor_NullEmail_ShouldReturnUser() throws SQLException {
        ResultSet mockRs = mock(ResultSet.class);
        when(mockRs.getLong(Constants.ID)).thenReturn(1L);
        when(mockRs.getString(Constants.FIRST_NAME)).thenReturn("John");
        when(mockRs.getString(Constants.LAST_NAME)).thenReturn("Doe");
        when(mockRs.getString(Constants.EMAIL)).thenReturn(null);
        when(mockRs.getString(Constants.PASSWORD)).thenReturn("password123");

        User user = new User(mockRs);
        assertNotNull(user);
    }
}