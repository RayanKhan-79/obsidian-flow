package com.example.frontend.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class ReportsController {

    @FXML private Button dashboardBtn;
    @FXML private Button projectsBtn;
    @FXML private Button tasksBtn;
    @FXML private Button teamBtn;
    @FXML private Button reportsBtn;
    @FXML private Button settingsBtn;
    @FXML private Button logoutBtn;
    
    @FXML private ComboBox<String> reportTypeComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Button generateReportBtn;
    @FXML private Label totalTasksLabel;
    @FXML private Label completedTasksLabel;
    @FXML private Label pendingTasksLabel;
    @FXML private Label overdueTasksLabel;
    @FXML private Label totalProjectsLabel;
    @FXML private Label totalMembersLabel;

    @FXML
    public void initialize() {
        System.out.println("✅ ReportsController initialized");
        setupNavigation();
        loadSampleData();
    }
    
    private void setupNavigation() {
        dashboardBtn.setOnAction(e -> loadPage("dashboard"));
        projectsBtn.setOnAction(e -> loadPage("projects"));
        tasksBtn.setOnAction(e -> loadPage("tasks"));
        teamBtn.setOnAction(e -> loadPage("team"));
        reportsBtn.setOnAction(e -> loadPage("reports"));
        settingsBtn.setOnAction(e -> loadPage("settings"));
        logoutBtn.setOnAction(e -> handleLogout());
        
        if (generateReportBtn != null) {
            generateReportBtn.setOnAction(e -> generateReport());
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
    
    private void loadSampleData() {
        if (totalTasksLabel != null) totalTasksLabel.setText("128");
        if (completedTasksLabel != null) completedTasksLabel.setText("86");
        if (pendingTasksLabel != null) pendingTasksLabel.setText("32");
        if (overdueTasksLabel != null) overdueTasksLabel.setText("10");
        if (totalProjectsLabel != null) totalProjectsLabel.setText("8");
        if (totalMembersLabel != null) totalMembersLabel.setText("12");
        
        if (reportTypeComboBox != null) {
            reportTypeComboBox.getItems().addAll(
                "Task Summary",
                "Project Progress",
                "Team Performance",
                "Time Tracking"
            );
            reportTypeComboBox.setValue("Task Summary");
        }
    }
    
    private void generateReport() {
        showInfo("Generate Report", "Report generation will be implemented here");
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