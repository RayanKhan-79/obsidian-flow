package com.example.backend.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class Database 
{
    String DB_URL;
    final Connection connection;
    static Database instance;
    

    // Call this method before the start of the program
    public static void initialzize(String file) {
        if (instance != null)
            return;

        instance = new Database(file);
    }

    public static Database GetInstance()
    {
        return instance;
    }

    private Database(String file)
    {
        try 
        {
            DB_URL = String.format("jdbc:sqlite:%s", file);
            connection = DriverManager.getConnection(DB_URL);
            CreateUsersTable();
            CreateActivityLogTable();
            CreateUserPermissionsTable();
            CreateProjectsTable();
            CreateTasksTable();
            EnsureTaskAssignmentColumn();
            CreateCommentsTable();
            CreateProjectMembershipTable();
            SeedDefaultAdmin();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private void CreateUsersTable() throws SQLException  
    {
        String sql = """ 
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                first_name TEXT NOT NULL,
                last_name TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL
            )
        """;
        executeUpdate(sql);
        System.out.println("User Table Created");
    }

    private void CreateActivityLogTable() throws SQLException
    {
        String sql = """
            CREATE TABLE IF NOT EXISTS activity_log (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                message TEXT NOT NULL,
                timestamp TEXT NOT NULL
            )
        """;
        executeUpdate(sql);
        System.out.println("Activity Log Table Created");
    }
            
    private void CreateUserPermissionsTable() throws SQLException
    {
        String sql = """
            CREATE TABLE IF NOT EXISTS user_permissions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER REFERENCES users(id) NOT NULL,
                permission TEXT NOT NULL
            )        
        """;
        executeUpdate(sql);
        System.out.println("Permissions Table Created");
    }

    private void CreateTasksTable() throws SQLException  
    {
        String sql = """    
            CREATE TABLE IF NOT EXISTS tasks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                project_id INTEGER REFERENCES projects(id),
                title TEXT NOT NULL,
                description TEXT,
                assigned_user_id INTEGER REFERENCES users(id),
                priority INT,
                status TEXT NOT NULL,
                due_date TEXT,
                created_date TEXT NOT NULL
            )
        """;
        executeUpdate(sql);
        System.out.println("Task Table Created");

    }

    private void EnsureTaskAssignmentColumn() {
        try {
            executeUpdate("ALTER TABLE tasks ADD COLUMN assigned_user_id INTEGER REFERENCES users(id)");
        } catch (SQLException ignored) {
            // Column likely already exists.
        }
    }

    private void CreateCommentsTable() throws SQLException
    {
        String sql = """
            CREATE TABLE IF NOT EXISTS comments (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                task_id INTEGER REFERENCES tasks(id),
                user_id INTEGER REFERENCES users(id),
                text TEXT NOT NULL,
                created_date TEXT NOT NULL
            )
        """;
        executeUpdate(sql);
        System.out.println("Comments Table Created");
    }

    // Many to Many Table Project - Project Members
    private void CreateProjectMembershipTable() throws SQLException
    {
        String sql = """
            CREATE TABLE IF NOT EXISTS project_memberships (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                project_member_id INTEGER REFERENCES users(id),
                project_id INTEGER REFERENCES projects(id),
                assignment_date TEXT NOT NULL
            )
        """;

        executeUpdate(sql);
        System.out.println("Project Membership Table Created");
    }

    private void CreateProjectsTable() throws SQLException
    {
        String sql = """
            CREATE TABLE IF NOT EXISTS projects (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                description TEXT,
                manager_id INTEGER REFERENCES users(id),
                created_date TEXT NOT NULL
            )
        """;

        executeUpdate(sql);
        System.out.println("Projects Table Created");
    }

    private void SeedDefaultAdmin() throws SQLException
    {
        ResultSet rs = executeQuery("SELECT id FROM users WHERE email = ? LIMIT 1", "admin@example.com");
        if (!rs.next()) {
            executeUpdate(
                "INSERT INTO users (first_name, last_name, email, password) VALUES (?, ?, ?, ?)",
                "System",
                "Admin",
                "admin@example.com",
                "admin123"
            );

            Long adminId = GetInsertedId();
            executeUpdate(
                "INSERT INTO user_permissions (user_id, permission) VALUES (?, ?)",
                adminId,
                "PROJECT_MANAGER"
            );
            executeUpdate(
                "INSERT INTO user_permissions (user_id, permission) VALUES (?, ?)",
                adminId,
                "Admin"
            );
        }
    }


    public Integer executeUpdate(String sql, Object... params) throws SQLException
    {    
        PreparedStatement pstmt = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            pstmt.setObject(i + 1, params[i]);
        }
        return pstmt.executeUpdate();
    }

    public ResultSet executeQuery(String sql, Object... params)
    {
        try 
        {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            
            return pstmt.executeQuery();
        } 
        catch (SQLException e) {
            System.out.println("SQL Error");
            throw new RuntimeException();
        }
    }

    public String GetUpdateSQL(Map<String, Object> updates) {
        var stream = updates.entrySet().stream();
        var mapped = stream.map((entry) -> {
            return String.format(
                "%s = %s", 
                entry.getKey().toString(), 
                entry.getValue().toString()
            );
        });
        var updateSql = String.join(",\n", mapped.toList());
        return updateSql;
    }

    public Long GetInsertedId()
    {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT last_insert_rowid()")) {
            var rs = stmt.executeQuery();
            
            if (rs.next()) 
                return rs.getLong(1);
            
            throw new RuntimeException();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
