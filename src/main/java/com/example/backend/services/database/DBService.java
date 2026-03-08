package com.example.backend.services.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import com.example.backend.model.User;

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
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private void CreateUserTable() throws SQLException  
    {
        String sql = "CREATE TABLE IF NOT EXISTS Users (" +
            "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "first_name TEXT NOT NULL," +
            "last_name TEXT NOT NULL," +
            "email TEXT NOT NULL UNIQUE," +
            "password TEXT NOT NULL" +
        ")";
        executeUpdate(sql);
        System.out.println("User Table Created");
    }

    private void CreateTaskTable() throws SQLException  
    {
        String sql = "CREATE TABLE IF NOT EXISTS Tasks (" +
            "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "title TEXT NOT NULL," +
            "description TEXT," +
            "status TEXT NOT NULL UNIQUE," +
            "created_date TEXT NOT NULL," +
            "due_date TEXT" +
        ")";
        executeUpdate(sql);
        System.out.println("Task Table Created");
    }

    protected int executeUpdate(String sql, Object... params) throws SQLException
    {    
            PreparedStatement pstmt = connection.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            return pstmt.executeUpdate();
    }

    protected ResultSet executeQuery(String sql, Object... params)
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

    public void CreateUser(Object... params) 
    {
        try 
        {
            String sql = "INSERT INTO USERS (first_name, last_name, email, password) VALUES (?, ?, ?, ?)";
            executeUpdate(sql, params);
        } catch (SQLException e) {
            System.out.println("could not create user invalid arguments");
            throw new RuntimeException();
        }
    }

    public Optional<User> FindUserById(Long Id)
    {
        String sql = "SELECT * FROM USERS WHERE id = ?";
        ResultSet result = executeQuery(sql, Id);

        return MapResultToModel(result, User.class);
    }

    
    public Optional<User> FindUserByEmailAndPassword(String email, String password)
    {
        String sql = "SELECT * FROM USERS WHERE email = ? AND password = ?";
        ResultSet result = executeQuery(sql, email, password);
        return MapResultToModel(result, User.class);
    }
    
    private <T> Optional<T> MapResultToModel(ResultSet result, Class<T> type) 
    {
        try 
        {
            var constructor = type.getConstructor(ResultSet.class);
            if (result.next())
                return Optional.of(constructor.newInstance(result));
        } 
        catch (Exception e) 
        {
            return Optional.empty();
        }
        
        return Optional.empty();
    }
}
