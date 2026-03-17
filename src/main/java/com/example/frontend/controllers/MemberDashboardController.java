package com.example.frontend.controllers;

import com.example.frontend.models.Task;
import com.example.frontend.models.User;
import com.example.frontend.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class MemberDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label userRoleLabel;
    @FXML private Label currentDateLabel;
    
    // Sidebar - Member specific navigation
    @FXML private Button dashboardBtn;
    @FXML private Button myTasksBtn;
    @FXML private Button myProjectsBtn;
    @FXML private Button notificationsBtn;
    @FXML private Button profileBtn;
    @FXML private Button logoutBtn;
    
    // Top bar
    @FXML private TextField searchField;
    @FXML private Label notificationIcon;
    @FXML private Label userProfileLabel;
    
    // Stats Cards - Member's personal stats
    @FXML private Label myTasksCount;
    @FXML private Label completedTasksCount;
    @FXML private Label pendingTasksCount;
    @FXML private Label overdueTasksCount;
    
    // My Assigned Tasks Section
    @FXML private TableView<Task> myTasksTable;
    @FXML private TableColumn<Task, String> taskNameColumn;
    @FXML private TableColumn<Task, String> projectColumn;
    @FXML private TableColumn<Task, String> priorityColumn;
    @FXML private TableColumn<Task, String> statusColumn;
    @FXML private TableColumn<Task, LocalDate> dueDateColumn;
    @FXML private TableColumn<Task, String> actionsColumn;
    
    // My Projects Section (Read-only)
    @FXML private ListView<String> myProjectsList;
    
    // Recent Comments & Mentions Section
    @FXML private ListView<String> mentionsListView;
    
    // Profile Section (Collapsible)
    @FXML private TitledPane profilePane;
    @FXML private Label memberNameLabel;
    @FXML private Label memberEmailLabel;
    @FXML private Label memberDepartmentLabel;
    @FXML private Label memberSinceLabel;
    @FXML private Button editProfileBtn;
    
    private User currentUser;
    private ObservableList<Task> myTasks = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        currentUser = SessionManager.getCurrentUser();
        
        if (currentUser == null) {
            System.err.println("❌ No user in session!");
            return;
        }
        
        System.out.println("✅ MemberDashboardController initialized for: " + currentUser.getFullName());
        
        setupUI();
        loadMyTasks();
        loadMyProjects();
        loadMentionsAndComments();
        setupProfileSection();
        setupNavigation();
        setupEventHandlers();
    }
    
    private void setupUI() {
        // Set welcome message
        welcomeLabel.setText("Hello, " + currentUser.getFullName() + "!");
        userRoleLabel.setText(currentUser.getRole() + " • " + 
                             (currentUser.getDepartment() != null ? currentUser.getDepartment() : "Team Member"));
        currentDateLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        userProfileLabel.setText("👤 " + currentUser.getFullName());
        
        // Setup table columns
        taskNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        projectColumn.setCellValueFactory(new PropertyValueFactory<>("project"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        
        // Setup actions column with View, Update Status, and Comment buttons
        actionsColumn.setCellFactory(col -> new TableCell<Task, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5);
                    buttons.setAlignment(Pos.CENTER);
                    
                    Task task = getTableView().getItems().get(getIndex());
                    
                    Button viewBtn = new Button("👁️");
                    viewBtn.setStyle("-fx-background-color: #6366f1; -fx-text-fill: white; -fx-padding: 5 8; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-size: 12px;");
                    viewBtn.setTooltip(new Tooltip("View task details"));
                    viewBtn.setOnAction(e -> handleViewTask(task));
                    
                    Button statusBtn = new Button("↻");
                    statusBtn.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-padding: 5 8; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-size: 12px;");
                    statusBtn.setTooltip(new Tooltip("Update status"));
                    statusBtn.setOnAction(e -> showStatusUpdateDialog(task));
                    
                    Button commentBtn = new Button("💬");
                    commentBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-padding: 5 8; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-size: 12px;");
                    commentBtn.setTooltip(new Tooltip("Add comment"));
                    commentBtn.setOnAction(e -> showCommentDialog(task));
                    
                    buttons.getChildren().addAll(viewBtn, statusBtn, commentBtn);
                    setGraphic(buttons);
                }
            }
        });
        
        myTasksTable.setItems(myTasks);
    }
    
    private void loadMyTasks() {
        myTasks.clear();
        
        // Sample tasks assigned to this member
        myTasks.addAll(
            new Task("Implement Login API", "⚡ High", "In Progress", "Mobile App", LocalDate.now().plusDays(2)),
            new Task("Fix Navigation Bug", "🔥 Critical", "To Do", "Mobile App", LocalDate.now().plusDays(1)),
            new Task("Update Documentation", "📊 Medium", "Done", "API Integration", LocalDate.now().minusDays(2)),
            new Task("Code Review", "📊 Medium", "In Progress", "Website", LocalDate.now().plusDays(3)),
            new Task("Write Unit Tests", "📉 Low", "To Do", "API Integration", LocalDate.now().plusDays(5))
        );
        
        updateTaskStats();
    }
    
    private void updateTaskStats() {
        int total = myTasks.size();
        int completed = 0;
        int pending = 0;
        int overdue = 0;
        
        for (Task task : myTasks) {
            if ("Done".equals(task.getStatus())) {
                completed++;
            } else {
                pending++;
            }
            
            if (task.getDeadline() != null && 
                task.getDeadline().isBefore(LocalDate.now()) && 
                !"Done".equals(task.getStatus())) {
                overdue++;
            }
        }
        
        myTasksCount.setText(String.valueOf(total));
        completedTasksCount.setText(String.valueOf(completed));
        pendingTasksCount.setText(String.valueOf(pending));
        overdueTasksCount.setText(String.valueOf(overdue));
    }
    
    private void loadMyProjects() {
        myProjectsList.getItems().clear();
        
        // Projects this member is part of (read-only)
        myProjectsList.getItems().addAll(
            "📱 Mobile App Development",
            "🔧 API Integration",
            "🌐 Website Redesign"
        );
        
        myProjectsList.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setTooltip(new Tooltip("Double-click to view project details"));
                }
            }
        });
        
        // Double-click to view project details (read-only)
        myProjectsList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String selected = myProjectsList.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    String projectName = selected.substring(2); // Remove emoji
                    showProjectDetails(projectName);
                }
            }
        });
    }
    
    private void loadMentionsAndComments() {
        mentionsListView.getItems().clear();
        
        // Comments and mentions for this member
        mentionsListView.getItems().addAll(
            "💬 @john - Sarah mentioned you in task 'Implement Login API' (2 hours ago)",
            "💬 @john - Mike replied to your comment (5 hours ago)",
            "✅ Your task 'Update Documentation' was approved (1 day ago)",
            "🔔 New comment on task you're watching (2 days ago)",
            "💬 @john - You were mentioned in project 'Mobile App' (3 days ago)"
        );
        
        mentionsListView.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(10);
                    box.setAlignment(Pos.CENTER_LEFT);
                    
                    String icon = item.substring(0, 2);
                    String text = item.substring(3);
                    
                    Label iconLabel = new Label(icon);
                    iconLabel.setStyle("-fx-font-size: 16px; -fx-min-width: 30;");
                    
                    Label textLabel = new Label(text);
                    textLabel.setWrapText(true);
                    textLabel.setStyle("-fx-text-fill: #334155; -fx-font-size: 13px;");
                    
                    box.getChildren().addAll(iconLabel, textLabel);
                    setGraphic(box);
                }
            }
        });
    }
    
    private void setupProfileSection() {
        memberNameLabel.setText(currentUser.getFullName());
        memberEmailLabel.setText(currentUser.getEmail());
        memberDepartmentLabel.setText(currentUser.getDepartment() != null ? 
                                      currentUser.getDepartment() : "Not specified");
        memberSinceLabel.setText(currentUser.getCreatedAt() != null ?
                                currentUser.getCreatedAt().format(DateTimeFormatter.ofPattern("MMMM d, yyyy")) :
                                "March 15, 2025");
        
        editProfileBtn.setOnAction(e -> handleEditProfile());
    }
    
    private void setupNavigation() {
        dashboardBtn.setOnAction(e -> refreshDashboard());
        myTasksBtn.setOnAction(e -> focusOnMyTasks());
        myProjectsBtn.setOnAction(e -> focusOnMyProjects());
        notificationsBtn.setOnAction(e -> focusOnNotifications());
        profileBtn.setOnAction(e -> expandProfile());
        logoutBtn.setOnAction(e -> handleLogout());
        
        searchField.textProperty().addListener((obs, old, newVal) -> handleSearch(newVal));
    }
    
    private void setupEventHandlers() {
        notificationIcon.setOnMouseClicked(e -> focusOnNotifications());
    }
    
    @FXML
    private void focusOnMyTasks() {
        System.out.println("📋 Focusing on my tasks");
        myTasksTable.requestFocus();
        if (!myTasks.isEmpty()) {
            myTasksTable.getSelectionModel().selectFirst();
        }
        myTasksTable.scrollTo(0);
    }
    
    @FXML
    private void focusOnMyProjects() {
        System.out.println("📁 Focusing on my projects");
        myProjectsList.requestFocus();
        if (!myProjectsList.getItems().isEmpty()) {
            myProjectsList.getSelectionModel().selectFirst();
        }
    }
    
    private void focusOnNotifications() {
        System.out.println("🔔 Opening notifications screen");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fxml/Notifications.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) notificationsBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Task Manager - Notifications");
            stage.centerOnScreen();
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open notifications: " + e.getMessage());
        }
    }
    
    private void expandProfile() {
        profilePane.setExpanded(true);
    }
    
    private void refreshDashboard() {
        loadMyTasks();
        loadMentionsAndComments();
        showInfo("Refreshed", "Dashboard updated");
    }
    
    private void handleViewTask(Task task) {
        try {
            System.out.println("🔍 Attempting to open task: " + task.getName());
            
            // Try multiple paths to find the FXML
            String[] possiblePaths = {
                "/com/example/fxml/TaskDetail.fxml",
                "/fxml/TaskDetail.fxml",
                "/TaskDetail.fxml"
            };
            
            java.net.URL fxmlUrl = null;
            for (String path : possiblePaths) {
                fxmlUrl = getClass().getResource(path);
                if (fxmlUrl != null) {
                    System.out.println("  ✅ Found at: " + path);
                    break;
                }
            }
            
            if (fxmlUrl == null) {
                System.err.println("❌ Could not find TaskDetail.fxml!");
                showAlert("Error", "Could not load task detail view");
                return;
            }
            
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            
            Object controller = loader.getController();
            if (controller == null) {
                System.err.println("❌ Controller is null!");
                showAlert("Error", "Controller not found");
                return;
            }
            
            // Use reflection to call setTask if available
            try {
                java.lang.reflect.Method setTaskMethod = controller.getClass().getMethod("setTask", Task.class);
                setTaskMethod.invoke(controller, task);
            } catch (Exception e) {
                System.out.println("⚠️ Could not call setTask method: " + e.getMessage());
            }
            
            Stage stage = new Stage();
            stage.setTitle("Task Details - " + task.getName());
            stage.setScene(new Scene(root));
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.show();
            
            System.out.println("✅ Task details window opened successfully");
            
        } catch (IOException e) {
            System.err.println("❌ IOException: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Could not open task details: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Unexpected error: " + e.getMessage());
        }
    }
    
    private void showCommentDialog(Task task) {
        // Create custom comment dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Comment");
        dialog.setHeaderText("Add comment to task: " + task.getName());
        
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogPane.setPrefWidth(450);
        dialogPane.setPrefHeight(300);
        dialogPane.setStyle("-fx-background-color: white;");
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        // Task info
        Label taskInfoLabel = new Label("📋 " + task.getName());
        taskInfoLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #1e293b;");
        
        Label projectLabel = new Label("Project: " + task.getProject());
        projectLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");
        
        Separator separator = new Separator();
        
        // Comment area
        Label commentLabel = new Label("Your Comment:");
        commentLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");
        
        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Write your comment... Use @ to mention someone (e.g., @sarah)");
        commentArea.setPrefRowCount(6);
        commentArea.setWrapText(true);
        commentArea.setStyle("-fx-border-color: #e2e8f0; -fx-border-radius: 5; -fx-padding: 8;");
        
        // Mention hint
        Label hintLabel = new Label("💡 Tip: Use @username to notify someone");
        hintLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px; -fx-font-style: italic;");
        
        content.getChildren().addAll(taskInfoLabel, projectLabel, separator, commentLabel, commentArea, hintLabel);
        dialogPane.setContent(content);
        
        // Set OK button styling
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.setText("Post Comment");
        okButton.setStyle("-fx-background-color: #6366f1; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20;");
        
        Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
        cancelButton.setStyle("-fx-background-color: #f1f5f9; -fx-padding: 8 20;");
        
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String commentText = commentArea.getText();
            if (commentText != null && !commentText.trim().isEmpty()) {
                // Add to mentions list
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
                mentionsListView.getItems().add(0, 
                    "💬 You commented on task '" + task.getName() + "' (" + timestamp + ")");
                
                showInfo("Success", "Comment added successfully");
                
                System.out.println("✅ Comment added to task " + task.getId() + ": " + commentText);
            } else {
                showAlert("Error", "Comment cannot be empty");
            }
        }
    }
    
    @FXML
    private void showCommentOnSelectedTask() {
        Task selectedTask = myTasksTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert("No Task Selected", "Please select a task from the table first");
            return;
        }
        showCommentDialog(selectedTask);
    }
    
    private void showStatusUpdateDialog(Task task) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(task.getStatus(), 
            "To Do", "In Progress", "Done", "Blocked");
        dialog.setTitle("Update Task Status");
        dialog.setHeaderText("Update status for: " + task.getName());
        dialog.setContentText("New status:");
        
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white;");
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newStatus = result.get();
            if (!newStatus.equals(task.getStatus())) {
                task.setStatus(newStatus);
                myTasksTable.refresh();
                updateTaskStats();
                
                // Add to mentions list as activity
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
                mentionsListView.getItems().add(0, 
                    "✅ You updated task '" + task.getName() + "' to " + newStatus + " (" + timestamp + ")");
                
                showInfo("Success", "Task status updated to " + newStatus);
            }
        }
    }
    
    private void showProjectDetails(String projectName) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Project Details");
        dialog.setHeaderText(projectName);
        
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.CLOSE);
        dialogPane.setPrefWidth(450);
        dialogPane.setStyle("-fx-background-color: white;");
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        Label infoLabel = new Label("📌 You are a member of this project (read-only)");
        infoLabel.setStyle("-fx-text-fill: #6366f1; -fx-font-weight: bold; -fx-font-size: 13px;");
        
        // Stats grid
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(10);
        statsGrid.setStyle("-fx-padding: 10 0;");
        
        statsGrid.add(new Label("Total Tasks:"), 0, 0);
        statsGrid.add(new Label("24"), 1, 0);
        statsGrid.add(new Label("Completed:"), 0, 1);
        statsGrid.add(new Label("16 (67%)"), 1, 1);
        statsGrid.add(new Label("In Progress:"), 0, 2);
        statsGrid.add(new Label("5"), 1, 2);
        statsGrid.add(new Label("Pending:"), 0, 3);
        statsGrid.add(new Label("3"), 1, 3);
        
        // Team members
        Label teamLabel = new Label("👥 Team Members:");
        teamLabel.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 5 0; -fx-font-size: 14px;");
        
        ListView<String> teamList = new ListView<>();
        teamList.setPrefHeight(100);
        teamList.getItems().addAll(
            "👤 Sarah Johnson (Project Manager)",
            "👤 John Doe (You)",
            "👤 Mike Chen (Member)",
            "👤 Alice Brown (Member)"
        );
        teamList.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0;");
        
        // Your tasks in this project
        Label yourTasksLabel = new Label("📋 Your Tasks in this Project:");
        yourTasksLabel.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 5 0; -fx-font-size: 14px;");
        
        ListView<String> tasksList = new ListView<>();
        tasksList.setPrefHeight(80);
        tasksList.getItems().addAll(
            "📋 Implement Login API (In Progress)",
            "📋 Fix Navigation Bug (To Do)",
            "📋 Write Unit Tests (Done)"
        );
        tasksList.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0;");
        
        content.getChildren().addAll(
            infoLabel,
            new Separator(),
            statsGrid,
            teamLabel,
            teamList,
            yourTasksLabel,
            tasksList
        );
        
        dialogPane.setContent(content);
        
        Button closeButton = (Button) dialogPane.lookupButton(ButtonType.CLOSE);
        closeButton.setStyle("-fx-background-color: #6366f1; -fx-text-fill: white; -fx-padding: 8 20; -fx-font-weight: bold;");
        
        dialog.showAndWait();
    }
    
    private void handleEditProfile() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Profile");
        dialog.setHeaderText("Update your profile information");
        
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogPane.setPrefWidth(450);
        dialogPane.setStyle("-fx-background-color: white;");
        
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));
        
        // Current values
        TextField nameField = new TextField(currentUser.getFullName());
        nameField.setPromptText("Full Name");
        nameField.setPrefWidth(250);
        nameField.setStyle("-fx-padding: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 5;");
        
        TextField emailField = new TextField(currentUser.getEmail());
        emailField.setPromptText("Email");
        emailField.setStyle("-fx-padding: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 5;");
        
        TextField departmentField = new TextField(currentUser.getDepartment());
        departmentField.setPromptText("Department");
        departmentField.setStyle("-fx-padding: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 5;");
        
        PasswordField currentPassField = new PasswordField();
        currentPassField.setPromptText("Current Password");
        currentPassField.setStyle("-fx-padding: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 5;");
        
        PasswordField newPassField = new PasswordField();
        newPassField.setPromptText("New Password (leave blank to keep current)");
        newPassField.setStyle("-fx-padding: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 5;");
        
        PasswordField confirmPassField = new PasswordField();
        confirmPassField.setPromptText("Confirm New Password");
        confirmPassField.setStyle("-fx-padding: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 5;");
        
        // Add labels and fields
        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Department:"), 0, 2);
        grid.add(departmentField, 1, 2);
        grid.add(new Label("Current Password:"), 0, 3);
        grid.add(currentPassField, 1, 3);
        grid.add(new Label("New Password:"), 0, 4);
        grid.add(newPassField, 1, 4);
        grid.add(new Label("Confirm Password:"), 0, 5);
        grid.add(confirmPassField, 1, 5);
        
        dialogPane.setContent(grid);
        
        // Style buttons
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.setText("Save Changes");
        okButton.setStyle("-fx-background-color: #6366f1; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20;");
        
        Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
        cancelButton.setStyle("-fx-background-color: #f1f5f9; -fx-padding: 8 20;");
        
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Validate
            if (nameField.getText().trim().isEmpty()) {
                showAlert("Error", "Name cannot be empty");
                return;
            }
            
            if (emailField.getText().trim().isEmpty() || !emailField.getText().contains("@")) {
                showAlert("Error", "Valid email is required");
                return;
            }
            
            // Check password change
            if (!newPassField.getText().isEmpty()) {
                if (currentPassField.getText().isEmpty()) {
                    showAlert("Error", "Current password is required to change password");
                    return;
                }
                if (!newPassField.getText().equals(confirmPassField.getText())) {
                    showAlert("Error", "New passwords do not match");
                    return;
                }
                if (newPassField.getText().length() < 6) {
                    showAlert("Error", "Password must be at least 6 characters");
                    return;
                }
            }
            
            // Update profile
            currentUser.setFullName(nameField.getText());
            currentUser.setEmail(emailField.getText());
            currentUser.setDepartment(departmentField.getText());
            
            // Update UI
            welcomeLabel.setText("Hello, " + currentUser.getFullName() + "!");
            userProfileLabel.setText("👤 " + currentUser.getFullName());
            memberNameLabel.setText(currentUser.getFullName());
            memberEmailLabel.setText(currentUser.getEmail());
            memberDepartmentLabel.setText(currentUser.getDepartment());
            
            showInfo("Success", "Profile updated successfully!");
        }
    }
    
    private void handleSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            myTasksTable.setItems(myTasks);
        } else {
            String lowerQuery = query.toLowerCase().trim();
            ObservableList<Task> filtered = FXCollections.observableArrayList();
            for (Task task : myTasks) {
                if (task.getName().toLowerCase().contains(lowerQuery) ||
                    (task.getProject() != null && task.getProject().toLowerCase().contains(lowerQuery))) {
                    filtered.add(task);
                }
            }
            myTasksTable.setItems(filtered);
            
            if (filtered.isEmpty()) {
                showInfo("Search", "No tasks match '" + query + "'");
            }
        }
    }
    
    private void handleLogout() {
        try {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Logout");
            confirm.setHeaderText(null);
            confirm.setContentText("Are you sure you want to logout?");
            
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                SessionManager.clearSession();
                Parent root = FXMLLoader.load(getClass().getResource("/com/example/fxml/Login.fxml"));
                Stage stage = (Stage) logoutBtn.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Task Manager - Login");
                stage.centerOnScreen();
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not logout: " + e.getMessage());
        }
    }
    
    private void showNotifications() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Quick Notifications");
        alert.setHeaderText(null);
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        
        Label titleLabel = new Label("Recent Notifications");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        ListView<String> notifList = new ListView<>();
        notifList.setPrefHeight(150);
        notifList.getItems().addAll(
            "🔔 @sarah mentioned you in a comment",
            "🔔 Task 'Implement Login API' is due in 2 days",
            "🔔 Mike replied to your comment",
            "🔔 You have 3 pending tasks",
            "🔔 New comment on task you're watching"
        );
        
        content.getChildren().addAll(titleLabel, notifList);
        alert.getDialogPane().setContent(content);
        alert.getDialogPane().setPrefWidth(350);
        
        alert.showAndWait();
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