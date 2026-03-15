package com.example.backend.services.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.example.backend.enums.UserTypes;
import com.example.backend.models.Task;
import com.example.backend.models.User;
import com.example.backend.util.Util;

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
            CreateUserTable();
            CreateTaskTable();
            CreateProjectMemberTable();
            CreateProjectManagerTable();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private void CreateUserTable() throws SQLException  
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

    private void CreateProjectMemberTable() throws SQLException
    {
        String sql = """ 
            CREATE TABLE IF NOT EXISTS Project_Members (
                Id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_Id INTEGER REFERENCES Users(Id)
            )
        """;
        executeUpdate(sql);
        System.out.println("Member Table Created");
    }

    private void CreateProjectManagerTable() throws SQLException
    {
        String sql = """ 
            CREATE TABLE IF NOT EXISTS Project_Managers (
                Id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_Id INTEGER REFERENCES Users(Id)
            )
        """;
        executeUpdate(sql);
        System.out.println("Manager Table Created");
    }

    private void CreateTaskTable() throws SQLException  
    {
        String sql = """    
            CREATE TABLE IF NOT EXISTS Tasks (
                Id INTEGER PRIMARY KEY AUTOINCREMENT,
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

    private int executeUpdate(String sql, Object... params) throws SQLException
    {    
        PreparedStatement pstmt = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            pstmt.setObject(i + 1, params[i]);
        }
        return pstmt.executeUpdate();
    }

    private ResultSet executeQuery(String sql, Object... params)
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

    public void CreateUser(UserTypes userType, Object... params) 
    {
        try 
        {
            executeUpdate(
                "INSERT INTO USERS (first_name, last_name, email, password, user_type) VALUES (?, ?, ?, ?, ?)",
                Stream.concat(Arrays.stream(params), Stream.of(userType.toString())).toArray()
            );
            if (userType == UserTypes.PROJECT_MANAGER)
                executeUpdate(
                    "INSERT INTO Project_Managers (user_Id) VALUES (?)", 
                    GetInsertedId()
                );
            if (userType == UserTypes.PROJECT_MEMBER)
                executeUpdate(
                    "INSERT INTO Project_Managers (user_Id) VALUES (?)", 
                    GetInsertedId()
                );
        } catch (SQLException e) {
            System.out.println("could not create user invalid arguments");
            throw new RuntimeException();
        }
    }

    public Optional<User> FindUserById(Long Id)
    {
        String sql = "SELECT * FROM USERS WHERE Id = ?";
        ResultSet result = executeQuery(sql, Id);

        return Util.MapResultToModel(result, User.class);
    }

    
    public Optional<User> FindUserByEmailAndPassword(String email, String password)
    {
        String sql = "SELECT * FROM USERS WHERE email = ? AND password = ?";
        ResultSet result = executeQuery(sql, email, password);
        return Util.MapResultToModel(result, User.class);
    }

    public Boolean CreateTask(Object... params)
    {
        try 
        {
            String sql = String.format(
                "INSERT INTO tasks (title, description, status, priority, due_date, created_date) VALUES (?, ?, ?, ?, ?, %)", 
                LocalDateTime.now().toString()
            );

            executeQuery(sql, params);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public Boolean DeleteTask(Long Id)
    {
        try 
        {
            String sql = "DELETE FROM tasks WHERE Id = ?";
            executeUpdate(sql, Id);
            return true;
            
        } catch (Exception e) {
            
            return false;
        }
    }

    public Optional<Task> FindTask(Long Id)
    {
        try 
        {
            String sql = "SELECT * FROM tasks WHERE Id = ?";
            ResultSet result = executeQuery(sql, Id);

            return Util.MapResultToModel(result, Task.class);

        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Boolean UpdateTask(Long Id, Map<String, Object> update)
    {
        try 
        {

            var stream = update.entrySet().stream();
            var mapped = stream.map((entry) -> {
                return String.format(
                    "%s = %s", 
                    entry.getKey().toString(), 
                    entry.getValue().toString()
                );
            });
            var updateSql = String.join(",\n", mapped.toList());


            String sql = String.format("UPDATE Tasks SET %s WHERE Id = %d", updateSql, Id);
            System.out.println(sql);
            // executeUpdate(sql);

            return true;    
        } catch (Exception e) {
            return false;
        }
    }
}
