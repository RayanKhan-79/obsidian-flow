package com.example.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class User {
    private int id;
    private String username;
    private String email;
    private String password; // In real app, store hashed password
    private String fullName;
    private String role; // "Admin", "Project Manager", "Member", "Viewer"
    private String department;
    private String avatar;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private List<Integer> projectIds; // Projects user is part of
    private List<Integer> assignedTaskIds; // Tasks assigned to user
    
    // In-memory database for demo purposes
    private static Map<String, User> usersByUsername = new HashMap<>();
    private static Map<String, User> usersByEmail = new HashMap<>();
    private static int nextId = 1;
    
    // Static initializer - create sample users
    static {
        // Create admin user
        User admin = new User("admin", "admin@example.com", "admin123", "System Admin", "Admin", "IT");
        admin.setId(nextId++);
        usersByUsername.put("admin", admin);
        usersByEmail.put("admin@example.com", admin);
        
        // Create project manager
        User pm = new User("sarah", "sarah@example.com", "password123", "Sarah Johnson", "Project Manager", "Product");
        pm.setId(nextId++);
        usersByUsername.put("sarah", pm);
        usersByEmail.put("sarah@example.com", pm);
        
        // Create team members
        User john = new User("john", "john@example.com", "password123", "John Doe", "Member", "Engineering");
        john.setId(nextId++);
        usersByUsername.put("john", john);
        usersByEmail.put("john@example.com", john);
        
        User mike = new User("mike", "mike@example.com", "password123", "Mike Chen", "Member", "Engineering");
        mike.setId(nextId++);
        usersByUsername.put("mike", mike);
        usersByEmail.put("mike@example.com", mike);
        
        User alice = new User("alice", "alice@example.com", "password123", "Alice Brown", "Member", "Design");
        alice.setId(nextId++);
        usersByUsername.put("alice", alice);
        usersByEmail.put("alice@example.com", alice);
        
        // Create viewer
        User bob = new User("bob", "bob@example.com", "password123", "Bob Wilson", "Viewer", "Marketing");
        bob.setId(nextId++);
        usersByUsername.put("bob", bob);
        usersByEmail.put("bob@example.com", bob);
    }
    
    public User() {
        this.projectIds = new ArrayList<>();
        this.assignedTaskIds = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }
    
    public User(String username, String email, String password, String fullName, String role, String department) {
        this();
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.department = department;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    
    public List<Integer> getProjectIds() { return projectIds; }
    public void setProjectIds(List<Integer> projectIds) { this.projectIds = projectIds; }
    public void addProjectId(int projectId) { this.projectIds.add(projectId); }
    public void removeProjectId(int projectId) { this.projectIds.remove(Integer.valueOf(projectId)); }
    
    public List<Integer> getAssignedTaskIds() { return assignedTaskIds; }
    public void setAssignedTaskIds(List<Integer> assignedTaskIds) { this.assignedTaskIds = assignedTaskIds; }
    public void addAssignedTaskId(int taskId) { this.assignedTaskIds.add(taskId); }
    public void removeAssignedTaskId(int taskId) { this.assignedTaskIds.remove(Integer.valueOf(taskId)); }
    
    /**
     * Register a new user
     * @param newUser The user to register
     * @return true if registration successful, false if username/email already exists
     */
    public static boolean registerUser(User newUser) {
        // Check if username already exists
        if (usersByUsername.containsKey(newUser.getUsername())) {
            System.out.println("❌ Username already exists: " + newUser.getUsername());
            return false;
        }
        
        // Check if email already exists
        if (usersByEmail.containsKey(newUser.getEmail())) {
            System.out.println("❌ Email already exists: " + newUser.getEmail());
            return false;
        }
        
        // Set new ID
        newUser.setId(nextId++);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setActive(true);
        
        // Add to maps
        usersByUsername.put(newUser.getUsername(), newUser);
        usersByEmail.put(newUser.getEmail(), newUser);
        
        System.out.println("✅ User registered successfully: " + newUser.getUsername());
        return true;
    }
    
    /**
     * Login user with username/email and password
     * @param usernameOrEmail Username or email
     * @param password Password
     * @return User object if login successful, null otherwise
     */
    public static User loginUser(String usernameOrEmail, String password) {
        System.out.println("🔐 Login attempt: " + usernameOrEmail);
        
        // Try to find by username first
        User user = usersByUsername.get(usernameOrEmail);
        
        // If not found by username, try by email
        if (user == null) {
            user = usersByEmail.get(usernameOrEmail);
        }
        
        // Check if user exists and password matches
        if (user != null && user.getPassword().equals(password)) {
            if (!user.isActive()) {
                System.out.println("❌ Account is deactivated: " + usernameOrEmail);
                return null;
            }
            
            // Update last login time
            user.setLastLogin(LocalDateTime.now());
            System.out.println("✅ Login successful: " + user.getFullName() + " (" + user.getRole() + ")");
            return user;
        }
        
        System.out.println("❌ Login failed: Invalid credentials");
        return null;
    }
    
    /**
     * Find user by ID
     * @param id User ID
     * @return User object if found, null otherwise
     */
    public static User findById(int id) {
        for (User user : usersByUsername.values()) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }
    
    /**
     * Find user by username
     * @param username Username
     * @return User object if found, null otherwise
     */
    public static User findByUsername(String username) {
        return usersByUsername.get(username);
    }
    
    /**
     * Find user by email
     * @param email Email
     * @return User object if found, null otherwise
     */
    public static User findByEmail(String email) {
        return usersByEmail.get(email);
    }
    
    /**
     * Get all users
     * @return List of all users
     */
    public static List<User> getAllUsers() {
        return new ArrayList<>(usersByUsername.values());
    }
    
    /**
     * Get users by role
     * @param role Role to filter by
     * @return List of users with specified role
     */
    public static List<User> getUsersByRole(String role) {
        List<User> result = new ArrayList<>();
        for (User user : usersByUsername.values()) {
            if (user.getRole().equals(role)) {
                result.add(user);
            }
        }
        return result;
    }
    
    /**
     * Get active users
     * @return List of active users
     */
    public static List<User> getActiveUsers() {
        List<User> result = new ArrayList<>();
        for (User user : usersByUsername.values()) {
            if (user.isActive()) {
                result.add(user);
            }
        }
        return result;
    }
    
    /**
     * Update user information
     * @param updatedUser User with updated information
     * @return true if update successful
     */
    public static boolean updateUser(User updatedUser) {
        User existing = findById(updatedUser.getId());
        if (existing == null) {
            return false;
        }
        
        // Update maps if username or email changed
        if (!existing.getUsername().equals(updatedUser.getUsername())) {
            usersByUsername.remove(existing.getUsername());
            usersByUsername.put(updatedUser.getUsername(), updatedUser);
        }
        
        if (!existing.getEmail().equals(updatedUser.getEmail())) {
            usersByEmail.remove(existing.getEmail());
            usersByEmail.put(updatedUser.getEmail(), updatedUser);
        }
        
        return true;
    }
    
    /**
     * Deactivate user account
     * @param userId ID of user to deactivate
     * @return true if deactivation successful
     */
    public static boolean deactivateUser(int userId) {
        User user = findById(userId);
        if (user != null) {
            user.setActive(false);
            return true;
        }
        return false;
    }
    
    /**
     * Activate user account
     * @param userId ID of user to activate
     * @return true if activation successful
     */
    public static boolean activateUser(int userId) {
        User user = findById(userId);
        if (user != null) {
            user.setActive(true);
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return fullName + " (" + role + ")" + (isActive ? "" : " [Inactive]");
    }
}