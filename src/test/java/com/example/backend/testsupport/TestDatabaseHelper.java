package com.example.backend.testsupport;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;

import com.example.backend.database.Database;
import com.example.backend.services.ActivityLogService;
import com.example.backend.services.AuthService;
import com.example.backend.services.ProjectService;
import com.example.backend.services.TaskService;

public final class TestDatabaseHelper {

    private TestDatabaseHelper() {
    }

    public static File createTestDatabaseFile() {
        try {
            Path root = Path.of("target", "test-db");
            Files.createDirectories(root);
            return Files.createTempFile(root, "obsidian-flow-test-", ".sqlite").toFile();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create test database file", e);
        }
    }

    public static void initializeDatabase(File file) {
        resetSingletons();
        Database.initialzize(file.getAbsolutePath());
    }

    public static void resetSingletons() {
        try {
            resetSingleton(Database.class, "instance");
            resetSingleton(AuthService.class, "instance");
            resetSingleton(ProjectService.class, "instance");
            resetSingleton(TaskService.class, "instance");
            resetSingleton(ActivityLogService.class, "instance");
        } catch (Exception e) {
            throw new RuntimeException("Failed to reset test singletons", e);
        }
    }

    private static void resetSingleton(Class<?> cls, String instanceFieldName) throws Exception {
        Field field = cls.getDeclaredField(instanceFieldName);
        field.setAccessible(true);
        Object current = field.get(null);
        if (current instanceof Database db) {
            closeDatabaseConnection(db);
        }
        field.set(null, null);
    }

    private static void closeDatabaseConnection(Database db) throws Exception {
        Field connectionField = Database.class.getDeclaredField("connection");
        connectionField.setAccessible(true);
        Connection connection = (Connection) connectionField.get(db);
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public static void cleanupDatabaseFile(File file) {
        if (file == null) {
            return;
        }

        try {
            resetSingletons();
            Files.deleteIfExists(file.toPath());
        } catch (Exception e) {
            throw new RuntimeException("Failed to clean up test database file", e);
        }
    }
}
