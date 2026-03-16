package com.example.models;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class User {
    private static Map<String, User> userDatabase = new HashMap<>(); // Simulated database
    
    private String username;
    private String email;
    private String password; // In real app, this would be hashed
    private String fullName;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private boolean isActive;

    // Constructor
    public User(String username, String email, String password, String fullName) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    // Static methods for user management (simulating database)
    public static boolean registerUser(User user) {
        if (userDatabase.containsKey(user.getUsername()) || 
            userDatabase.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            return false; // User already exists
        }
        userDatabase.put(user.getUsername(), user);
        return true;
    }

    public static User loginUser(String usernameOrEmail, String password) {
        // Check by username
        User user = userDatabase.get(usernameOrEmail);
        if (user != null && user.getPassword().equals(password)) {
            user.setLastLogin(LocalDateTime.now());
            return user;
        }
        
        // Check by email
        for (User u : userDatabase.values()) {
            if (u.getEmail().equals(usernameOrEmail) && u.getPassword().equals(password)) {
                u.setLastLogin(LocalDateTime.now());
                return u;
            }
        }
        return null;
    }

    // Add a demo user for testing
    static {
        // Create a demo user for testing
        User demoUser = new User("demo", "demo@example.com", "password123", "Demo User");
        userDatabase.put("demo", demoUser);
    }
}