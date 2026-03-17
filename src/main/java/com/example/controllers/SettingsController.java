package com.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class SettingsController {

    @FXML private Button dashboardBtn;
    @FXML private Button projectsBtn;
    @FXML private Button tasksBtn;
    @FXML private Button teamBtn;
    @FXML private Button reportsBtn;
    @FXML private Button settingsBtn;
    @FXML private Button logoutBtn;
    
    @FXML private TabPane settingsTabPane;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> themeComboBox;
    @FXML private CheckBox emailNotificationsCheck;
    @FXML private CheckBox pushNotificationsCheck;
    @FXML private Button saveProfileBtn;
    @FXML private Button changePasswordBtn;
    @FXML private Button savePreferencesBtn;

    @FXML
    public void initialize() {
        System.out.println("✅ SettingsController initialized");
        setupNavigation();
        loadUserSettings();
    }
    
    private void setupNavigation() {
        dashboardBtn.setOnAction(e -> loadPage("dashboard"));
        projectsBtn.setOnAction(e -> loadPage("projects"));
        tasksBtn.setOnAction(e -> loadPage("tasks"));
        teamBtn.setOnAction(e -> loadPage("team"));
        reportsBtn.setOnAction(e -> loadPage("reports"));
        settingsBtn.setOnAction(e -> loadPage("settings"));
        logoutBtn.setOnAction(e -> handleLogout());
        
        if (saveProfileBtn != null) {
            saveProfileBtn.setOnAction(e -> saveProfile());
        }
        
        if (changePasswordBtn != null) {
            changePasswordBtn.setOnAction(e -> changePassword());
        }
        
        if (savePreferencesBtn != null) {
            savePreferencesBtn.setOnAction(e -> savePreferences());
        }
    }
    
    private void loadPage(String page) {
        try {
            String fxmlFile = "/com/example/fxml/" + page.substring(0,1).toUpperCase() + page.substring(1) + ".fxml";
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) dashboardBtn.getScene().getWindow();
            stage.setScene(new Scene(root,1200,800));
            stage.setTitle("Task Manager - " + page.substring(0,1).toUpperCase() + page.substring(1));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load " + page + " page");
        }
    }
    
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/fxml/Login.fxml"));
            Stage stage = (Stage) logoutBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Task Manager - Login");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadUserSettings() {
        // Load sample user data
        if (fullNameField != null) fullNameField.setText("John Doe");
        if (emailField != null) emailField.setText("john.doe@example.com");
        
        if (themeComboBox != null) {
            themeComboBox.getItems().addAll("Light", "Dark", "System Default");
            themeComboBox.setValue("Light");
        }
        
        if (emailNotificationsCheck != null) emailNotificationsCheck.setSelected(true);
        if (pushNotificationsCheck != null) pushNotificationsCheck.setSelected(false);
    }
    
    private void saveProfile() {
        String name = fullNameField.getText();
        String email = emailField.getText();
        
        if (name.isEmpty() || email.isEmpty()) {
            showAlert("Error", "Name and email cannot be empty");
            return;
        }
        
        if (!email.contains("@") || !email.contains(".")) {
            showAlert("Error", "Please enter a valid email address");
            return;
        }
        
        showInfo("Success", "Profile updated successfully!");
    }
    
    private void changePassword() {
        String current = currentPasswordField.getText();
        String newPass = newPasswordField.getText();
        String confirm = confirmPasswordField.getText();
        
        if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            showAlert("Error", "All password fields are required");
            return;
        }
        
        if (!newPass.equals(confirm)) {
            showAlert("Error", "New password and confirm password do not match");
            return;
        }
        
        if (newPass.length() < 6) {
            showAlert("Error", "Password must be at least 6 characters long");
            return;
        }
        
        showInfo("Success", "Password changed successfully!");
        
        // Clear fields
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }
    
    private void savePreferences() {
        String theme = themeComboBox.getValue();
        boolean emailNotif = emailNotificationsCheck.isSelected();
        boolean pushNotif = pushNotificationsCheck.isSelected();
        
        System.out.println("Saving preferences: Theme=" + theme + 
                          ", Email=" + emailNotif + 
                          ", Push=" + pushNotif);
        
        showInfo("Success", "Preferences saved successfully!");
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}