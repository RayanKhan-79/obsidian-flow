package com.example.frontend.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;

import com.example.frontend.utils.DatabaseUtil;

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
    @FXML private TextArea reportOutputArea;

    @FXML
    public void initialize() {
        System.out.println("✅ ReportsController initialized");
        setupNavigation();
        initializeReportFilters();
        refreshSummary();
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
            String[] paths = {
                "/com/example/fxml/" + page + ".fxml",
                "/com/example/fxml/" + page.substring(0,1).toUpperCase() + page.substring(1) + ".fxml"
            };

            java.net.URL fxmlUrl = null;
            for (String path : paths) {
                fxmlUrl = getClass().getResource(path);
                if (fxmlUrl != null) break;
            }

            if (fxmlUrl == null) {
                showAlert("Error", "Could not load " + page + " page");
                return;
            }

            Parent root = FXMLLoader.load(fxmlUrl);
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
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/fxml/login.fxml"));
            Stage stage = (Stage) logoutBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Task Manager - Login");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void initializeReportFilters() {
        if (reportTypeComboBox != null) {
            reportTypeComboBox.getItems().addAll(
                "Task Summary",
                "Project Progress",
                "Team Performance",
                "Time Tracking"
            );
            reportTypeComboBox.setValue("Task Summary");
        }

        if (startDatePicker != null) {
            startDatePicker.setValue(LocalDate.now().minusDays(30));
        }

        if (endDatePicker != null) {
            endDatePicker.setValue(LocalDate.now());
        }
    }

    private void refreshSummary() {
        var summary = DatabaseUtil.buildReportSummary(
            startDatePicker == null ? null : startDatePicker.getValue(),
            endDatePicker == null ? null : endDatePicker.getValue()
        );

        if (totalTasksLabel != null) totalTasksLabel.setText(String.valueOf(summary.totalTasks()));
        if (completedTasksLabel != null) completedTasksLabel.setText(String.valueOf(summary.completedTasks()));
        if (pendingTasksLabel != null) pendingTasksLabel.setText(String.valueOf(summary.pendingTasks()));
        if (overdueTasksLabel != null) overdueTasksLabel.setText(String.valueOf(summary.overdueTasks()));
        if (totalProjectsLabel != null) totalProjectsLabel.setText(String.valueOf(summary.totalProjects()));
        if (totalMembersLabel != null) totalMembersLabel.setText(String.valueOf(summary.totalMembers()));
    }
    
    private void generateReport() {
        refreshSummary();

        String reportType = reportTypeComboBox == null ? "Task Summary" : reportTypeComboBox.getValue();
        LocalDate start = startDatePicker == null ? null : startDatePicker.getValue();
        LocalDate end = endDatePicker == null ? null : endDatePicker.getValue();

        var summary = DatabaseUtil.buildReportSummary(start, end);
        String text = String.format(
            "Report Type: %s%nPeriod: %s to %s%n%nTotal Tasks: %d%nCompleted: %d%nPending: %d%nOverdue: %d%nProjects: %d%nTeam Members: %d",
            reportType,
            start == null ? "-" : start,
            end == null ? "-" : end,
            summary.totalTasks(),
            summary.completedTasks(),
            summary.pendingTasks(),
            summary.overdueTasks(),
            summary.totalProjects(),
            summary.totalMembers()
        );

        if (reportOutputArea != null) {
            reportOutputArea.setText(text);
        }

        showInfo("Report Generated", reportType + " has been generated from live task/project data.");
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