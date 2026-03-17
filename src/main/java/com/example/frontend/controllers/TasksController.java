package com.example.frontend.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import javafx.beans.property.SimpleStringProperty;

import com.example.frontend.models.Task;

public class TasksController {

    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> taskNameCol;
    @FXML private TableColumn<Task, String> assignedCol;
    @FXML private TableColumn<Task, String> priorityCol;
    @FXML private TableColumn<Task, String> statusCol;
    @FXML private TableColumn<Task, String> deadlineCol;
    
    @FXML private Button dashboardBtn;
    @FXML private Button projectsBtn;
    @FXML private Button tasksBtn;
    @FXML private Button teamBtn;
    @FXML private Button reportsBtn;
    @FXML private Button settingsBtn;
    @FXML private Button logoutBtn;
    @FXML private Button newTaskButton;
    @FXML private ComboBox<String> projectFilter;
    @FXML private TextField searchField;
    @FXML private Label totalTasksLabel;

    private static ObservableList<Task> tasks = FXCollections.observableArrayList();
    private ObservableList<Task> filteredTasks = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        System.out.println("✅ TasksController initialized");
        
        setupTableColumns();
        loadSampleTasks();
        setupProjectFilter();
        setupNavigation();
        setupSearch();
        updateTotalTasks();
    }
    
    private void setupTableColumns() {
        // Use SimpleStringProperty callbacks to avoid module issues
        taskNameCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getName()));
        
        assignedCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getAssignedTo()));
        
        priorityCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getPriority()));
        
        statusCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatus()));
        
        deadlineCol.setCellValueFactory(cellData -> {
            Task task = cellData.getValue();
            if (task.getDeadline() != null) {
                return new SimpleStringProperty(task.getDeadline().toString());
            } else {
                return new SimpleStringProperty("No deadline");
            }
        });
        
        taskTable.setItems(tasks);
    }
    
    private void loadSampleTasks() {
        if (tasks.isEmpty()) {
            tasks.addAll(
                new Task("Implement Login", "John", "High", "In Progress", LocalDate.now().plusDays(2)),
                new Task("Design Database", "Sarah", "Medium", "To Do", LocalDate.now().plusDays(5)),
                new Task("Create UI Mockups", "Mike", "High", "Done", LocalDate.now().minusDays(1)),
                new Task("Write Documentation", "Alice", "Low", "To Do", LocalDate.now().plusDays(7)),
                new Task("API Integration", "John", "High", "In Progress", LocalDate.now().plusDays(3)),
                new Task("Testing", "Bob", "Medium", "To Do", LocalDate.now().plusDays(4))
            );
        }
        updateTotalTasks();
    }
    
    private void setupProjectFilter() {
        if (projectFilter != null) {
            // Get unique project names from tasks
            ObservableList<String> projects = FXCollections.observableArrayList();
            projects.add("All Projects");
            
            for (Task task : tasks) {
                String project = task.getProject();
                if (project != null && !projects.contains(project)) {
                    projects.add(project);
                }
            }
            
            projectFilter.setItems(projects);
            projectFilter.setValue("All Projects");
            
            projectFilter.setOnAction(e -> filterByProject(projectFilter.getValue()));
        }
    }
    
    private void setupSearch() {
        if (searchField != null) {
            searchField.textProperty().addListener((obs, old, newVal) -> {
                handleSearch(newVal);
            });
        }
    }
    
    private void setupNavigation() {
        if (dashboardBtn != null) {
            dashboardBtn.setOnAction(e -> loadPage("dashboard"));
            projectsBtn.setOnAction(e -> loadPage("projects"));
            tasksBtn.setOnAction(e -> loadPage("tasks"));
            teamBtn.setOnAction(e -> loadPage("team"));
            reportsBtn.setOnAction(e -> loadPage("reports"));
            settingsBtn.setOnAction(e -> loadPage("settings"));
            logoutBtn.setOnAction(e -> handleLogout());
        }
        
        if (newTaskButton != null) {
            newTaskButton.setOnAction(e -> openAddTaskDialog());
        }
    }
    
    private void loadPage(String page) {
        try {
            String fxmlFile = "/com/example/fxml/" + page.substring(0,1).toUpperCase() + page.substring(1) + ".fxml";
            System.out.println("Loading: " + fxmlFile);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            
            Stage stage = (Stage) dashboardBtn.getScene().getWindow();
            stage.setScene(new Scene(root,1200,800));
            stage.setTitle("Task Manager - " + page.substring(0,1).toUpperCase() + page.substring(1));
            stage.centerOnScreen();
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load " + page + " page: " + e.getMessage());
        }
    }
    
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fxml/Login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) logoutBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Task Manager - Login");
            stage.centerOnScreen();
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not logout: " + e.getMessage());
        }
    }

    @FXML
    private void openAddTaskDialog() {
        try {
            String[] paths = {
                "/com/example/fxml/addTask.fxml",
                "/fxml/addTask.fxml",
                "/addTask.fxml"
            };
            
            java.net.URL fxmlUrl = null;
            for (String path : paths) {
                fxmlUrl = getClass().getResource(path);
                if (fxmlUrl != null) {
                    System.out.println("✅ Found addTask.fxml at: " + path);
                    break;
                }
            }
            
            if (fxmlUrl == null) {
                showAlert("Error", "Add Task form not found");
                return;
            }
            
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Add New Task");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // Refresh table
            taskTable.setItems(tasks);
            taskTable.refresh();
            updateTotalTasks();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open add task dialog: " + e.getMessage());
        }
    }

    public static ObservableList<Task> getTasks() {
        return tasks;
    }

    public void filterByProject(String projectName) {
        if (projectName == null || projectName.equals("All Projects")) {
            taskTable.setItems(tasks);
        } else {
            filteredTasks.clear();
            for (Task task : tasks) {
                if (projectName.equals(task.getProject())) {
                    filteredTasks.add(task);
                }
            }
            taskTable.setItems(filteredTasks);
        }
        updateTotalTasks();
    }
    
    private void handleSearch(String query) {
        ObservableList<Task> currentList = projectFilter.getValue().equals("All Projects") ? tasks : filteredTasks;
        
        if (query == null || query.trim().isEmpty()) {
            if (projectFilter.getValue().equals("All Projects")) {
                taskTable.setItems(tasks);
            } else {
                taskTable.setItems(filteredTasks);
            }
        } else {
            String lowerQuery = query.toLowerCase().trim();
            ObservableList<Task> searchResults = FXCollections.observableArrayList();
            
            for (Task task : currentList) {
                if (task.getName().toLowerCase().contains(lowerQuery) ||
                    (task.getAssignedTo() != null && task.getAssignedTo().toLowerCase().contains(lowerQuery)) ||
                    (task.getProject() != null && task.getProject().toLowerCase().contains(lowerQuery))) {
                    searchResults.add(task);
                }
            }
            taskTable.setItems(searchResults);
        }
        updateTotalTasks();
    }
    
    private void updateTotalTasks() {
        if (totalTasksLabel != null) {
            int count = taskTable.getItems().size();
            totalTasksLabel.setText("Total Tasks: " + count);
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