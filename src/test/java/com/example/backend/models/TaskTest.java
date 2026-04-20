package com.example.backend.models;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.example.backend.database.Constants;
import com.example.backend.enums.TaskStatus;

/**
 * Test class for Task model
 *
 * Black-box test cases:
 * - Equivalence partitioning: valid data, invalid data
 * - Error guessing: SQL exceptions, null values, invalid date formats
 *
 * White-box coverage:
 * - Constructor branches
 */
public class TaskTest {

    @Test
    void testConstructor_ValidData_ShouldParseCorrectly() throws SQLException {
        ResultSet mockRs = mock(ResultSet.class);
        when(mockRs.getLong(Constants.ID)).thenReturn(1L);
        when(mockRs.getLong(Constants.PROJECT_ID)).thenReturn(1L);
        when(mockRs.getString(Constants.TITLE)).thenReturn("Test Task");
        when(mockRs.getString(Constants.DESCRIPTION)).thenReturn("Description");
        when(mockRs.getString(Constants.STATUS)).thenReturn("PENDING");
        when(mockRs.getLong(Constants.PRIORITY)).thenReturn(1L);
        when(mockRs.getString(Constants.DUE_DATE)).thenReturn("2026-12-31T23:59:59");
        when(mockRs.getString(Constants.CREATED_DATE)).thenReturn("2026-01-01T00:00:00");

        Task task = new Task(mockRs);

        assertEquals(1L, task.Id);
        assertEquals(1L, task.project_id);
        assertEquals("Test Task", task.title);
        assertEquals(TaskStatus.PENDING, task.status);
    }

    @Test
    void testConstructor_SQLException_ShouldThrowRuntimeException() throws SQLException {
        ResultSet mockRs = mock(ResultSet.class);
        when(mockRs.getLong(anyString())).thenThrow(SQLException.class);

        assertThrows(RuntimeException.class, () -> new Task(mockRs));
    }

    @Test
    void testConstructor_InvalidDateFormat_ShouldThrowRuntimeException() throws SQLException {
        ResultSet mockRs = mock(ResultSet.class);
        when(mockRs.getLong(Constants.ID)).thenReturn(1L);
        when(mockRs.getLong(Constants.PROJECT_ID)).thenReturn(1L);
        when(mockRs.getString(Constants.TITLE)).thenReturn("Test Task");
        when(mockRs.getString(Constants.DESCRIPTION)).thenReturn("Description");
        when(mockRs.getString(Constants.STATUS)).thenReturn("PENDING");
        when(mockRs.getLong(Constants.PRIORITY)).thenReturn(1L);
        when(mockRs.getString(Constants.DUE_DATE)).thenReturn("invalid-date");
        when(mockRs.getString(Constants.CREATED_DATE)).thenReturn("2026-01-01T00:00:00");

        assertThrows(RuntimeException.class, () -> new Task(mockRs));
    }

    @Test
    void testConstructor_InvalidTaskStatus_ShouldThrowRuntimeException() throws SQLException {
        ResultSet mockRs = mock(ResultSet.class);
        when(mockRs.getLong(Constants.ID)).thenReturn(1L);
        when(mockRs.getLong(Constants.PROJECT_ID)).thenReturn(1L);
        when(mockRs.getString(Constants.TITLE)).thenReturn("Test Task");
        when(mockRs.getString(Constants.DESCRIPTION)).thenReturn("Description");
        when(mockRs.getString(Constants.STATUS)).thenReturn("INVALID_STATUS");
        when(mockRs.getLong(Constants.PRIORITY)).thenReturn(1L);
        when(mockRs.getString(Constants.CREATED_DATE)).thenReturn("2026-01-01T00:00:00");

        assertThrows(RuntimeException.class, () -> new Task(mockRs));
    }
}
