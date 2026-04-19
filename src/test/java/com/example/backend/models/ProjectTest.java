package com.example.backend.models;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.example.backend.database.Constants;

/**
 * Test class for Project model
 *
 * Black-box test cases:
 * - Equivalence partitioning: valid data, invalid data
 * - Error guessing: SQL exceptions, null values, invalid dates
 *
 * White-box coverage:
 * - Constructor branches
 */
public class ProjectTest {

    @Test
    void testConstructor_ValidData_ShouldParseCorrectly() throws SQLException {
        ResultSet mockRs = mock(ResultSet.class);
        when(mockRs.getLong(Constants.ID)).thenReturn(1L);
        when(mockRs.getString(Constants.TITLE)).thenReturn("Test Project");
        when(mockRs.getString(Constants.DESCRIPTION)).thenReturn("Description");
        when(mockRs.getLong(Constants.MANAGER_ID)).thenReturn(1L);
        when(mockRs.getString(Constants.CREATED_DATE)).thenReturn("2026-01-01T00:00:00");

        Project project = new Project(mockRs);

        assertEquals(1L, project.Id);
        assertEquals("Test Project", project.title);
        assertEquals("Description", project.description);
        assertEquals(1L, project.manager_id);
        assertNotNull(project.createdDate);
    }

    @Test
    void testConstructor_SQLException_ShouldThrowRuntimeException() throws SQLException {
        ResultSet mockRs = mock(ResultSet.class);
        when(mockRs.getLong(anyString())).thenThrow(SQLException.class);

        assertThrows(RuntimeException.class, () -> new Project(mockRs));
    }

    @Test
    void testConstructor_InvalidDateFormat_ShouldThrowRuntimeException() throws SQLException {
        ResultSet mockRs = mock(ResultSet.class);
        when(mockRs.getLong(Constants.ID)).thenReturn(1L);
        when(mockRs.getString(Constants.TITLE)).thenReturn("Test Project");
        when(mockRs.getString(Constants.DESCRIPTION)).thenReturn("Description");
        when(mockRs.getLong(Constants.MANAGER_ID)).thenReturn(1L);
        when(mockRs.getString(Constants.CREATED_DATE)).thenReturn("invalid-date");

        assertThrows(RuntimeException.class, () -> new Project(mockRs));
    }
}
