package com.example.backend.services.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class DBService 
{
    final String DB_URL = "jdbc:sqlite:obsidian-flow.db";
    final Connection connection;
    static DBService instance;
    
    public static DBService GetInstance()
    {
        if (instance == null)
            instance = new DBService();

        return instance;
    }

    private DBService()
    {
        try 
        {
            connection = DriverManager.getConnection(DB_URL);
            CreateUsersTable();
            CreateProjectsTable();
            CreateTasksTable();
            CreateProjectMembersTable();
            CreateProjectManagersTable();
            CreateProjectMembershipTable();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private void CreateUsersTable() throws SQLException  
    {
        String sql = """ 
            CREATE TABLE IF NOT EXISTS Users (
                Id INTEGER PRIMARY KEY AUTOINCREMENT,
                first_name TEXT NOT NULL,
                last_name TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                user_type TEXT NOT NULL
            )
        """;
        executeUpdate(sql);
        System.out.println("User Table Created");
    }

    private void CreateProjectMembersTable() throws SQLException
    {
        String sql = """ 
            CREATE TABLE IF NOT EXISTS Project_Members (
                Id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER REFERENCES Users(Id)
            )
        """;
        executeUpdate(sql);
        System.out.println("Member Table Created");
    }

    private void CreateProjectManagersTable() throws SQLException
    {
        String sql = """ 
            CREATE TABLE IF NOT EXISTS Project_Managers (
                Id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER REFERENCES Users(Id)
            )
        """;
        executeUpdate(sql);
        System.out.println("Manager Table Created");
    }

    private void CreateTasksTable() throws SQLException  
    {
        String sql = """    
            CREATE TABLE IF NOT EXISTS Tasks (
                Id INTEGER PRIMARY KEY AUTOINCREMENT,
                project_id INTEGER REFERENCES Projects(Id),
                title TEXT NOT NULL,
                description TEXT,
                priority INT,
                status TEXT NOT NULL UNIQUE,
                due_date TEXT,
                created_date TEXT NOT NULL
        )
        """;
        executeUpdate(sql);
        System.out.println("Task Table Created");

    }

    // Many to Many Table Project - Project Members
    private void CreateProjectMembershipTable() throws SQLException
    {
        String sql = """
            CREATE TABLE IF NOT EXISTS Project_Memberships (
                Id INTEGER PRIMARY KEY AUTOINCREMENT,
                project_member_id INTEGER REFERENCES Users(Id),
                project_id INTEGER REFERENCES Projects(Id),
                assignment_date TEXT NOT NULL
            )
        """;

        executeUpdate(sql);
        System.out.println("Project Membership Table Created");
    }

    private void CreateProjectsTable() throws SQLException
    {
        String sql = """
            CREATE TABLE IF NOT EXISTS Projects (
                Id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                manager_id INTEGER REFERENCES Users(Id),
                created_date TEXT NOT NULL,
            )
        """;

        executeUpdate(sql);
        System.out.println("Projects Table Created");
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
