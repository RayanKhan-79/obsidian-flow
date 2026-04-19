package com.example.frontend.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

import com.example.frontend.models.Project;
import com.example.frontend.utils.DatabaseUtil;

public class ProjectsController {

    @FXML private Button dashboardBtn;
    @FXML private Button projectsBtn;
    @FXML private Button tasksBtn;
    @FXML private Button teamBtn;
    @FXML private Button reportsBtn;
    @FXML private Button settingsBtn;
    @FXML private Button logoutBtn;
    
    @FXML private TextField searchField;
    @FXML private Button newProjectButton;
    @FXML private Label totalProjectsLabel;
    @FXML private ListView<HBox> projectsListView;

    @FXML
    public void initialize() {
        setupNavigation();
        loadProjects();
    }
    
    private void setupNavigation() {
        dashboardBtn.setOnAction(e -> loadPage("dashboard"));
        projectsBtn.setOnAction(e -> loadPage("projects"));
        tasksBtn.setOnAction(e -> loadPage("tasks"));
        teamBtn.setOnAction(e -> loadPage("team"));
        reportsBtn.setOnAction(e -> loadPage("reports"));
        settingsBtn.setOnAction(e -> loadPage("settings"));
        logoutBtn.setOnAction(e -> handleLogout());
        
        newProjectButton.setOnAction(e -> handleNewProject());
        searchField.textProperty().addListener((obs, old, newVal) -> filterProjects(newVal));
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
                if (fxmlUrl != null) {
                    break;
                }
            }

            if (fxmlUrl == null) {
                showAlert("Error", "Could not load page: " + page);
                return;
            }

            Parent root = FXMLLoader.load(fxmlUrl);
            Stage stage = (Stage) dashboardBtn.getScene().getWindow();
            stage.setScene(new Scene(root,1200,800));
            stage.setTitle("Task Manager - " + page.substring(0,1).toUpperCase() + page.substring(1));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/fxml/login.fxml"));
            Stage stage = (Stage) logoutBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Task Manager - Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
// Add this method to ProjectsController.java
private void refreshProjectsList() {
    // This will be called after project creation
    loadProjects();
}

// Update handleNewProject method to refresh the list
@FXML
private void handleNewProject() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fxml/CreateProject.fxml"));
        Parent root = loader.load();
        
        Stage stage = new Stage();
        stage.setTitle("Create New Project");
        stage.setScene(new Scene(root));
        stage.showAndWait();
        
        // Refresh projects list after dialog closes
        refreshProjectsList();
        
    } catch (IOException e) {
        e.printStackTrace();
        showAlert("Error", "Could not open create project dialog");
    }
}

// Add this helper method for alerts
private void showAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}
    
    private void loadProjects() {
        projectsListView.getItems().clear();

        for (Project project : DatabaseUtil.getCurrentUserProjects()) {
            addProjectItem(
                project.getName(),
                "Owner ID: " + project.getOwnerId(),
                "0/" + project.getTotalTasks() + " tasks",
                project.getColor() == null ? "#6366f1" : project.getColor()
            );
        }

        totalProjectsLabel.setText("Total Projects: " + projectsListView.getItems().size());
    }
    
    private void addProjectItem(String name, String members, String tasks, String color) {
        HBox item = new HBox(20);
        item.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        item.setPrefWidth(800);
        
        Label colorIndicator = new Label("  ");
        colorIndicator.setStyle("-fx-background-color: " + color + "; -fx-pref-width: 10; -fx-pref-height: 50; -fx-background-radius: 5;");
        
        VBox infoBox = new VBox(5);
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label detailsLabel = new Label(members + "  |  " + tasks);
        detailsLabel.setStyle("-fx-text-fill: #64748b;");
        infoBox.getChildren().addAll(nameLabel, detailsLabel);
        
        Button membersBtn = new Button("👥 Members");
        membersBtn.setStyle("-fx-background-color: #f1f5f9; -fx-padding: 8 15; -fx-background-radius: 5; -fx-cursor: hand;");
        membersBtn.setOnAction(e -> handleManageMembers(name));
        
        Button tasksBtn = new Button("📋 Tasks");
        tasksBtn.setStyle("-fx-background-color: #f1f5f9; -fx-padding: 8 15; -fx-background-radius: 5; -fx-cursor: hand;");
        tasksBtn.setOnAction(e -> handleViewTasks(name));
        
        HBox buttonBox = new HBox(10, membersBtn, tasksBtn);
        buttonBox.setStyle("-fx-alignment: center-right;");
        
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        item.getChildren().addAll(colorIndicator, infoBox, buttonBox);
        projectsListView.getItems().add(item);
    }
    
private void handleManageMembers(String projectName) {
    try {
        System.out.println("📂 Opening manage members for: " + projectName);
        
        // Try multiple paths for AddMemberDialog.fxml
        String[] possiblePaths = {
            "/com/example/fxml/AddMemberDialog.fxml",
            "/fxml/AddMemberDialog.fxml",
            "/AddMemberDialog.fxml",
            "/com/example/fxml/AddMember.fxml"
        };
        
        java.net.URL fxmlUrl = null;
        for (String path : possiblePaths) {
            fxmlUrl = getClass().getResource(path);
            if (fxmlUrl != null) {
                System.out.println("✅ Found AddMemberDialog.fxml at: " + path);
                break;
            }
        }
        
        if (fxmlUrl == null) {
            System.err.println("❌ Could not find AddMemberDialog.fxml in any location!");
            showAlert("Error", "Could not find Add Member form");
            return;
        }
        
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();
        
        // Get the controller and set project name
        Object controller = loader.getController();
        if (controller instanceof AddMemberDialogController) {
            AddMemberDialogController dialogController = (AddMemberDialogController) controller;
            dialogController.setProjectName(projectName);
            
            // You can also pass existing members if needed
            // dialogController.setExistingMembers(getProjectMembers(projectName));
        } else {
            System.out.println("⚠️ Controller is not AddMemberDialogController: " + 
                              (controller != null ? controller.getClass().getName() : "null"));
        }
        
        Stage stage = new Stage();
        stage.setTitle("Manage Members - " + projectName);
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.showAndWait();
        
    } catch (IOException e) {
        System.err.println("❌ Error loading AddMemberDialog.fxml: " + e.getMessage());
        e.printStackTrace();
        showAlert("Error", "Could not open manage members: " + e.getMessage());
    } catch (Exception e) {
        System.err.println("❌ Unexpected error: " + e.getMessage());
        e.printStackTrace();
        showAlert("Error", "Unexpected error: " + e.getMessage());
    }
}
    
    private void handleViewTasks(String projectName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fxml/tasks.fxml"));
            Parent root = loader.load();
            
            TasksController controller = loader.getController();
            controller.filterByProject(projectName);
            
            Stage stage = (Stage) projectsBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Task Manager - Tasks (" + projectName + ")");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void filterProjects(String query) {
        if (query == null || query.isEmpty()) {
            loadProjects();
        } else {
            // Filter logic would go here
        }
    }

    public void filterByProject(String projectName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'filterByProject'");
    }
}