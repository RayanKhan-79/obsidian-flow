package com.example.controllers;

import com.example.models.User;
import com.example.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class DashboardController {
    
    // ============= LEFT SIDEBAR COMPONENTS =============
    @FXML private Button dashboardBtn;
    @FXML private Button tasksBtn;
    @FXML private Button teamBtn;
    @FXML private Button reportsBtn;
    @FXML private Button settingsBtn;
    @FXML private Button logoutBtn;
    
    // ============= TOP BAR COMPONENTS =============
    @FXML private Label dashboardTitle;
    @FXML private TextField searchField;
    @FXML private Label notificationIcon;
    @FXML private Label userProfileLabel;
    @FXML private HBox topBar;
    
    // ============= STAT CARDS =============
    @FXML private Label totalTasksValue;
    @FXML private Label completedTasksValue;
    @FXML private Label pendingTasksValue;
    @FXML private Label overdueTasksValue;
    
    // ============= TASK TABLE =============
    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> taskNameColumn;
    @FXML private TableColumn<Task, String> priorityColumn;
    @FXML private TableColumn<Task, String> statusColumn;
    @FXML private TableColumn<Task, String> dueDateColumn;
    @FXML private Button newTaskButton;
    
    // ============= DATA =============
    private ObservableList<Task> tasks = FXCollections.observableArrayList();
    private User currentUser;

    @FXML
    public void initialize() {
        try {
            System.out.println("Initializing Dashboard...");
            
            // Get current user
            currentUser = SessionManager.getCurrentUser();
            
            // Setup UI with null checks
            setupSidebar();
            setupTopBar();
            loadDashboardData();
            setupTaskTable();
            updateStatCards();
            
            System.out.println("Dashboard initialized successfully!");
            
        } catch (Exception e) {
            System.err.println("Error initializing dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupSidebar() {
        try {
            // Create array of buttons only if they're not null
            Button[] sidebarButtons = {dashboardBtn, tasksBtn, teamBtn, reportsBtn, settingsBtn};
            
            // Style for active button (Dashboard is active by default)
            if (dashboardBtn != null) {
                setActiveButton(dashboardBtn);
            }
            
            // Add hover effects to sidebar buttons
            for (Button btn : sidebarButtons) {
                if (btn != null) {
                    addSidebarHoverEffect(btn);
                    
                    // Set default style for non-active buttons
                    if (btn != dashboardBtn) {
                        btn.setStyle(
                            "-fx-background-color: transparent;" +
                            "-fx-text-fill: white;" +
                            "-fx-pref-width: 180;" +
                            "-fx-padding: 10 15;" +
                            "-fx-background-radius: 8;" +
                            "-fx-cursor: hand;"
                        );
                    }
                }
            }
            
            // Setup logout button
            if (logoutBtn != null) {
                setupLogoutButton();
            }
        } catch (Exception e) {
            System.err.println("Error setting up sidebar: " + e.getMessage());
        }
    }

    private void addSidebarHoverEffect(Button btn) {
        if (btn == null) return;
        
        btn.setOnMouseEntered(e -> {
            if (!isActiveButton(btn)) { // If not active
                btn.setStyle(
                    "-fx-background-color: #2d3a4f;" +
                    "-fx-text-fill: white;" +
                    "-fx-pref-width: 180;" +
                    "-fx-padding: 10 15;" +
                    "-fx-background-radius: 8;" +
                    "-fx-cursor: hand;"
                );
            }
        });
        
        btn.setOnMouseExited(e -> {
            if (!isActiveButton(btn)) { // If not active
                btn.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-text-fill: white;" +
                    "-fx-pref-width: 180;" +
                    "-fx-padding: 10 15;" +
                    "-fx-background-radius: 8;" +
                    "-fx-cursor: hand;"
                );
            }
        });
        
        btn.setOnAction(e -> handleSidebarNavigation(btn));
    }

    private boolean isActiveButton(Button btn) {
        return btn != null && btn.getStyle() != null && btn.getStyle().contains("#334155");
    }

    private void setActiveButton(Button activeBtn) {
        if (activeBtn == null) return;
        
        Button[] sidebarButtons = {dashboardBtn, tasksBtn, teamBtn, reportsBtn, settingsBtn};
        
        for (Button btn : sidebarButtons) {
            if (btn == null) continue;
            
            if (btn == activeBtn) {
                btn.setStyle(
                    "-fx-background-color: #334155;" +
                    "-fx-text-fill: white;" +
                    "-fx-pref-width: 180;" +
                    "-fx-padding: 10 15;" +
                    "-fx-background-radius: 8;" +
                    "-fx-cursor: hand;"
                );
            } else {
                btn.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-text-fill: white;" +
                    "-fx-pref-width: 180;" +
                    "-fx-padding: 10 15;" +
                    "-fx-background-radius: 8;" +
                    "-fx-cursor: hand;"
                );
            }
        }
    }

    private void setupLogoutButton() {
        logoutBtn.setStyle(
            "-fx-background-color: #ef4444;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 20;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        
        logoutBtn.setOnMouseEntered(e -> 
            logoutBtn.setStyle(
                "-fx-background-color: #dc2626;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10 20;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            )
        );
        
        logoutBtn.setOnMouseExited(e -> 
            logoutBtn.setStyle(
                "-fx-background-color: #ef4444;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10 20;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            )
        );
        
        logoutBtn.setOnAction(e -> handleLogout());
    }

    private void handleSidebarNavigation(Button clickedBtn) {
        if (clickedBtn == null) return;
        
        setActiveButton(clickedBtn);
        
        if (clickedBtn == dashboardBtn && dashboardTitle != null) {
            dashboardTitle.setText("Dashboard");
            // Refresh dashboard view
        } else if (clickedBtn == tasksBtn && dashboardTitle != null) {
            dashboardTitle.setText("Tasks");
            showAlert(Alert.AlertType.INFORMATION, "Tasks", "Tasks view coming soon!");
        } else if (clickedBtn == teamBtn && dashboardTitle != null) {
            dashboardTitle.setText("Team");
            showAlert(Alert.AlertType.INFORMATION, "Team", "Team view coming soon!");
        } else if (clickedBtn == reportsBtn && dashboardTitle != null) {
            dashboardTitle.setText("Reports");
            showAlert(Alert.AlertType.INFORMATION, "Reports", "Reports view coming soon!");
        } else if (clickedBtn == settingsBtn && dashboardTitle != null) {
            dashboardTitle.setText("Settings");
            showAlert(Alert.AlertType.INFORMATION, "Settings", "Settings view coming soon!");
        }
    }

    private void setupTopBar() {
        try {
            // Setup search field
            if (searchField != null) {
                searchField.setPromptText("🔍 Search tasks...");
                searchField.textProperty().addListener((obs, old, newVal) -> {
                    filterTasks(newVal);
                });
            }
            
            // Setup notification icon
            if (notificationIcon != null) {
                notificationIcon.setStyle("-fx-font-size: 18px; -fx-cursor: hand;");
                notificationIcon.setOnMouseClicked(e -> showNotifications());
            }
            
            // Setup user profile
            if (userProfileLabel != null) {
                if (currentUser != null) {
                    userProfileLabel.setText("👤 " + currentUser.getFullName().split(" ")[0]);
                } else {
                    userProfileLabel.setText("👤 Guest User");
                }
                userProfileLabel.setStyle("-fx-font-weight: bold; -fx-cursor: hand;");
                userProfileLabel.setOnMouseClicked(e -> showUserProfile());
            }
        } catch (Exception e) {
            System.err.println("Error setting up top bar: " + e.getMessage());
        }
    }

    private void loadDashboardData() {
        // Load demo tasks
        tasks.addAll(
            new Task("Design Dashboard UI", "High", "In Progress", LocalDate.now().plusDays(2)),
            new Task("Implement Authentication", "Critical", "Completed", LocalDate.now().minusDays(1)),
            new Task("Create Database Schema", "Medium", "Pending", LocalDate.now().plusDays(3)),
            new Task("Write Documentation", "Low", "Pending", LocalDate.now().plusDays(5)),
            new Task("Fix Login Bug", "Critical", "In Progress", LocalDate.now()),
            new Task("User Testing", "High", "Pending", LocalDate.now().plusDays(1)),
            new Task("Deploy to Production", "High", "Pending", LocalDate.now().plusDays(7)),
            new Task("Security Audit", "Critical", "Overdue", LocalDate.now().minusDays(2))
        );
    }

    private void setupTaskTable() {
        try {
            if (taskTable == null) return;
            
            // Setup columns
            if (taskNameColumn != null) {
                taskNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            }
            
            if (priorityColumn != null) {
                priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
                
                // Style priority column with colors
                priorityColumn.setCellFactory(column -> new TableCell<Task, String>() {
                    @Override
                    protected void updateItem(String priority, boolean empty) {
                        super.updateItem(priority, empty);
                        if (empty || priority == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(priority);
                            switch (priority) {
                                case "Critical":
                                    setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                                    break;
                                case "High":
                                    setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                                    break;
                                case "Medium":
                                    setStyle("-fx-text-fill: #3b82f6; -fx-font-weight: bold;");
                                    break;
                                case "Low":
                                    setStyle("-fx-text-fill: #22c55e; -fx-font-weight: bold;");
                                    break;
                            }
                        }
                    }
                });
            }
            
            if (statusColumn != null) {
                statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
                
                // Style status column with badges
                statusColumn.setCellFactory(column -> new TableCell<Task, String>() {
                    @Override
                    protected void updateItem(String status, boolean empty) {
                        super.updateItem(status, empty);
                        if (empty || status == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            Label badge = new Label(status);
                            badge.setPadding(new Insets(3, 8, 3, 8));
                            badge.setStyle("-fx-background-radius: 12;");
                            
                            switch (status) {
                                case "Completed":
                                    badge.setStyle(badge.getStyle() + "-fx-background-color: #22c55e20; -fx-text-fill: #22c55e;");
                                    break;
                                case "In Progress":
                                    badge.setStyle(badge.getStyle() + "-fx-background-color: #3b82f620; -fx-text-fill: #3b82f6;");
                                    break;
                                case "Pending":
                                    badge.setStyle(badge.getStyle() + "-fx-background-color: #f59e0b20; -fx-text-fill: #f59e0b;");
                                    break;
                                case "Overdue":
                                    badge.setStyle(badge.getStyle() + "-fx-background-color: #ef444420; -fx-text-fill: #ef4444;");
                                    break;
                            }
                            
                            setGraphic(badge);
                            setText(null);
                        }
                    }
                });
            }
            
            if (dueDateColumn != null) {
                dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDateFormatted"));
            }
            
            // Add double-click to edit task
            taskTable.setRowFactory(tv -> {
                TableRow<Task> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && (!row.isEmpty())) {
                        Task task = row.getItem();
                        editTask(task);
                    }
                });
                return row;
            });
            
            taskTable.setItems(tasks);
            
        } catch (Exception e) {
            System.err.println("Error setting up task table: " + e.getMessage());
        }
    }

    private void updateStatCards() {
        try {
            long total = tasks.size();
            long completed = tasks.stream().filter(t -> "Completed".equals(t.getStatus())).count();
            long pending = tasks.stream().filter(t -> "Pending".equals(t.getStatus())).count();
            long overdue = tasks.stream().filter(t -> "Overdue".equals(t.getStatus())).count();
            
            if (totalTasksValue != null) totalTasksValue.setText(String.valueOf(total));
            if (completedTasksValue != null) completedTasksValue.setText(String.valueOf(completed));
            if (pendingTasksValue != null) pendingTasksValue.setText(String.valueOf(pending));
            if (overdueTasksValue != null) overdueTasksValue.setText(String.valueOf(overdue));
            
        } catch (Exception e) {
            System.err.println("Error updating stat cards: " + e.getMessage());
        }
    }

    private void filterTasks(String searchText) {
        try {
            if (taskTable == null) return;
            
            if (searchText == null || searchText.isEmpty()) {
                taskTable.setItems(tasks);
            } else {
                ObservableList<Task> filtered = FXCollections.observableArrayList();
                for (Task task : tasks) {
                    if (task.getName().toLowerCase().contains(searchText.toLowerCase())) {
                        filtered.add(task);
                    }
                }
                taskTable.setItems(filtered);
            }
        } catch (Exception e) {
            System.err.println("Error filtering tasks: " + e.getMessage());
        }
    }

    @FXML
    private void handleNewTask() {
        try {
            Dialog<Task> dialog = new Dialog<>();
            dialog.setTitle("Create New Task");
            dialog.setHeaderText("Add a new task to your list");
            
            ButtonType createButton = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(createButton, ButtonType.CANCEL);
            
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 25, 10, 25));
            
            TextField nameField = new TextField();
            nameField.setPromptText("Task name");
            
            ComboBox<String> priorityCombo = new ComboBox<>();
            priorityCombo.getItems().addAll("Low", "Medium", "High", "Critical");
            priorityCombo.setValue("Medium");
            
            ComboBox<String> statusCombo = new ComboBox<>();
            statusCombo.getItems().addAll("Pending", "In Progress", "Completed", "Overdue");
            statusCombo.setValue("Pending");
            
            DatePicker dueDatePicker = new DatePicker();
            dueDatePicker.setValue(LocalDate.now().plusDays(3));
            
            grid.add(new Label("Task Name:"), 0, 0);
            grid.add(nameField, 1, 0);
            grid.add(new Label("Priority:"), 0, 1);
            grid.add(priorityCombo, 1, 1);
            grid.add(new Label("Status:"), 0, 2);
            grid.add(statusCombo, 1, 2);
            grid.add(new Label("Due Date:"), 0, 3);
            grid.add(dueDatePicker, 1, 3);
            
            dialog.getDialogPane().setContent(grid);
            
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == createButton) {
                    return new Task(
                        nameField.getText(),
                        priorityCombo.getValue(),
                        statusCombo.getValue(),
                        dueDatePicker.getValue()
                    );
                }
                return null;
            });
            
            Optional<Task> result = dialog.showAndWait();
            result.ifPresent(task -> {
                tasks.add(task);
                updateStatCards();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Task created successfully!");
            });
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not create task: " + e.getMessage());
        }
    }

    private void editTask(Task task) {
        try {
            Dialog<Task> dialog = new Dialog<>();
            dialog.setTitle("Edit Task");
            dialog.setHeaderText("Edit task: " + task.getName());
            
            ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);
            
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 25, 10, 25));
            
            TextField nameField = new TextField(task.getName());
            
            ComboBox<String> priorityCombo = new ComboBox<>();
            priorityCombo.getItems().addAll("Low", "Medium", "High", "Critical");
            priorityCombo.setValue(task.getPriority());
            
            ComboBox<String> statusCombo = new ComboBox<>();
            statusCombo.getItems().addAll("Pending", "In Progress", "Completed", "Overdue");
            statusCombo.setValue(task.getStatus());
            
            DatePicker dueDatePicker = new DatePicker(task.getDueDate());
            
            grid.add(new Label("Task Name:"), 0, 0);
            grid.add(nameField, 1, 0);
            grid.add(new Label("Priority:"), 0, 1);
            grid.add(priorityCombo, 1, 1);
            grid.add(new Label("Status:"), 0, 2);
            grid.add(statusCombo, 1, 2);
            grid.add(new Label("Due Date:"), 0, 3);
            grid.add(dueDatePicker, 1, 3);
            
            dialog.getDialogPane().setContent(grid);
            
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButton) {
                    task.setName(nameField.getText());
                    task.setPriority(priorityCombo.getValue());
                    task.setStatus(statusCombo.getValue());
                    task.setDueDate(dueDatePicker.getValue());
                    return task;
                }
                return null;
            });
            
            Optional<Task> result = dialog.showAndWait();
            result.ifPresent(updatedTask -> {
                taskTable.refresh();
                updateStatCards();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Task updated successfully!");
            });
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not edit task: " + e.getMessage());
        }
    }

    private void showNotifications() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notifications");
        alert.setHeaderText("Your Notifications");
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        
        content.getChildren().addAll(
            new Label("• Task 'Security Audit' is overdue"),
            new Label("• 3 tasks are due today"),
            new Label("• Jane commented on your task"),
            new Label("• New team member joined")
        );
        
        alert.getDialogPane().setContent(content);
        alert.showAndWait();
    }

    private void showUserProfile() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("User Profile");
        alert.setHeaderText(null);
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        
        if (currentUser != null) {
            content.getChildren().addAll(
                new Label("Name: " + currentUser.getFullName()),
                new Label("Username: " + currentUser.getUsername()),
                new Label("Email: " + currentUser.getEmail()),
                new Label("Member since: " + 
                    (currentUser.getCreatedAt() != null ? 
                     currentUser.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM d, yyyy")) : 
                     "N/A"))
            );
        } else {
            content.getChildren().add(new Label("Guest User"));
        }
        
        alert.getDialogPane().setContent(content);
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleLogout() {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Logout");
        confirmDialog.setHeaderText(null);
        confirmDialog.setContentText("Are you sure you want to logout?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                SessionManager.clearSession();
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fxml/login.fxml"));
                Parent root = loader.load();
                
                Stage stage = (Stage) logoutBtn.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Task Manager - Login");
                stage.centerOnScreen();
                
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Could not logout: " + e.getMessage());
            }
        }
    }
}

// Task model class
class Task {
    private String name;
    private String priority;
    private String status;
    private LocalDate dueDate;

    public Task(String name, String priority, String status, LocalDate dueDate) {
        this.name = name;
        this.priority = priority;
        this.status = status;
        this.dueDate = dueDate;
    }

    // Getters
    public String getName() { return name != null ? name : ""; }
    public String getPriority() { return priority != null ? priority : ""; }
    public String getStatus() { return status != null ? status : ""; }
    public LocalDate getDueDate() { return dueDate; }
    
    // Formatted getter for table
    public String getDueDateFormatted() {
        return dueDate != null ? dueDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy")) : "";
    }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setPriority(String priority) { this.priority = priority; }
    public void setStatus(String status) { this.status = status; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
}