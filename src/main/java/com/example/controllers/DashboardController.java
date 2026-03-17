package com.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.example.models.Task;

public class DashboardController {

    @FXML private Button dashboardBtn;
    @FXML private Button tasksBtn;
    @FXML private Button projectsBtn;
    @FXML private Button teamBtn;
    @FXML private Button reportsBtn;
    @FXML private Button settingsBtn;
    @FXML private Button logoutBtn;
    
    @FXML private Label dashboardTitle;
    @FXML private TextField searchField;
    @FXML private Label notificationIcon;
    @FXML private Label userProfileLabel;
    
    @FXML private Label totalTasksValue;
    @FXML private Label completedTasksValue;
    @FXML private Label pendingTasksValue;
    @FXML private Label overdueTasksValue;
    
    @FXML private Button newTaskButton;
    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> taskNameColumn;
    @FXML private TableColumn<Task, String> priorityColumn;
    @FXML private TableColumn<Task, String> statusColumn;
    @FXML private TableColumn<Task, LocalDate> dueDateColumn;
    
    @FXML private ComboBox<String> projectFilter;
    @FXML private Label projectCompletionLabel;

    @FXML
    public void initialize() {
        System.out.println("✅ DashboardController initialized");
        
        // Setup table columns
        setupTableColumns();
        
        // Load sample data
        loadDashboardData();
        
        // Setup navigation
        setupNavigation();
    }
    
    private void setupTableColumns() {
        try {
            taskNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        } catch (Exception e) {
            System.out.println("Error setting up table columns: " + e.getMessage());
        }
    }
    
    private void loadDashboardData() {
        try {
            // In real app, this would come from database
            totalTasksValue.setText("128");
            completedTasksValue.setText("86");
            pendingTasksValue.setText("32");
            overdueTasksValue.setText("10");
            
            // Update completion percentage
            int total = 128;
            int completed = 86;
            int percentage = (completed * 100) / total;
            if (projectCompletionLabel != null) {
                projectCompletionLabel.setText("Overall Progress: " + percentage + "%");
                projectCompletionLabel.setVisible(true);
            }
            
            // Load sample tasks
            ObservableList<Task> sampleTasks = FXCollections.observableArrayList(
                new Task("Implement Login", "High", "In Progress", "Mobile App", LocalDate.now().plusDays(2)),
                new Task("Design Database", "Medium", "To Do", "Mobile App", LocalDate.now().plusDays(5)),
                new Task("Create UI Mockups", "High", "Done", "Website", LocalDate.now().minusDays(1)),
                new Task("Write Documentation", "Low", "To Do", "API", LocalDate.now().plusDays(7))
            );
            taskTable.setItems(sampleTasks);
            
        } catch (Exception e) {
            System.out.println("Error loading dashboard data: " + e.getMessage());
        }
    }
    
    private void setupNavigation() {
        try {
            dashboardBtn.setOnAction(e -> loadPage("dashboard"));
            tasksBtn.setOnAction(e -> loadPage("tasks"));
            projectsBtn.setOnAction(e -> loadPage("projects"));
            teamBtn.setOnAction(e -> loadPage("team"));
            reportsBtn.setOnAction(e -> loadPage("reports"));
            settingsBtn.setOnAction(e -> loadPage("settings"));
            logoutBtn.setOnAction(e -> handleLogout());
            
            newTaskButton.setOnAction(e -> handleNewTask());
            searchField.textProperty().addListener((obs, old, newVal) -> handleSearch(newVal));
            
            System.out.println("✅ Navigation setup complete");
        } catch (Exception e) {
            System.out.println("Error setting up navigation: " + e.getMessage());
        }
    }
    
    private void loadPage(String page) {
        try {
            System.out.println("📂 Loading page: " + page);
            
            String fxmlFile;
            String title;
            
            // Determine which FXML to load based on page parameter
            switch(page) {
                case "dashboard":
                    fxmlFile = "/com/example/fxml/dashboard.fxml";
                    title = "Task Manager - Dashboard";
                    break;
                case "projects":
                    fxmlFile = "/com/example/fxml/Projects.fxml";
                    title = "Task Manager - Projects";
                    break;
                case "tasks":
                    fxmlFile = "/com/example/fxml/Tasks.fxml";
                    title = "Task Manager - Tasks";
                    break;
                case "team":
                    fxmlFile = "/com/example/fxml/Team.fxml";
                    title = "Task Manager - Team";
                    break;
                case "reports":
                    fxmlFile = "/com/example/fxml/Reports.fxml";
                    title = "Task Manager - Reports";
                    break;
                case "settings":
                    fxmlFile = "/com/example/fxml/Settings.fxml";
                    title = "Task Manager - Settings";
                    break;
                default:
                    fxmlFile = "/com/example/fxml/dashboard.fxml";
                    title = "Task Manager - Dashboard";
            }
            
            // Try multiple paths
            java.net.URL fxmlUrl = getClass().getResource(fxmlFile);
            
            if (fxmlUrl == null) {
                // Try without /com/example prefix
                String altPath = fxmlFile.replace("/com/example/fxml/", "/fxml/");
                fxmlUrl = getClass().getResource(altPath);
            }
            
            if (fxmlUrl == null) {
                // Try with lowercase
                String lowerPath = "/fxml/" + page.toLowerCase() + ".fxml";
                fxmlUrl = getClass().getResource(lowerPath);
            }
            
            if (fxmlUrl == null) {
                System.err.println("❌ Could not find FXML file for: " + page);
                showAlert("Error", "Could not load " + page + " page");
                return;
            }
            
            System.out.println("✅ Found at: " + fxmlUrl);
            
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            
            Stage stage = (Stage) dashboardBtn.getScene().getWindow();
            Scene scene = new Scene(root,1200,800);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.centerOnScreen();
            stage.show();
            
            System.out.println("✅ Page loaded successfully: " + page);
            
        } catch (IOException e) {
            System.err.println("❌ Error loading page: " + page);
            e.printStackTrace();
            showAlert("Error", "Could not load page: " + page + "\n" + e.getMessage());
        }
    }
    
    @FXML
    private void handleNewTask() {
        try {
            System.out.println("Opening new task dialog...");
            
            String[] paths = {
                "/com/example/fxml/addTask.fxml",
                "/fxml/addTask.fxml",
                "/addTask.fxml",
                "/com/example/fxml/AddTask.fxml"
            };
            
            java.net.URL fxmlUrl = null;
            for (String path : paths) {
                fxmlUrl = getClass().getResource(path);
                if (fxmlUrl != null) {
                    System.out.println("✅ Found at: " + path);
                    break;
                }
            }
            
            if (fxmlUrl == null) {
                showAlert("Error", "Create Task form not found");
                return;
            }
            
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Create New Task");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // Refresh data after task creation
            loadDashboardData();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open task creation form");
        }
    }
    
    private void handleSearch(String query) {
        if (query != null && !query.isEmpty()) {
            String lowerQuery = query.toLowerCase();
            ObservableList<Task> filtered = FXCollections.observableArrayList();
            for (Task task : taskTable.getItems()) {
                if (task.getName().toLowerCase().contains(lowerQuery)) {
                    filtered.add(task);
                }
            }
            taskTable.setItems(filtered);
        } else {
            loadDashboardData();
        }
    }
    
    private void handleLogout() {
        try {
            System.out.println("Logging out...");
            
            String[] paths = {
                "/com/example/fxml/Login.fxml",
                "/fxml/Login.fxml",
                "/Login.fxml"
            };
            
            java.net.URL fxmlUrl = null;
            for (String path : paths) {
                fxmlUrl = getClass().getResource(path);
                if (fxmlUrl != null) {
                    System.out.println("✅ Found at: " + path);
                    break;
                }
            }
            
            if (fxmlUrl == null) {
                showAlert("Error", "Login screen not found");
                return;
            }
            
            Parent root = FXMLLoader.load(fxmlUrl);
            Stage stage = (Stage) logoutBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Task Manager - Login");
            stage.centerOnScreen();
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not logout");
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}