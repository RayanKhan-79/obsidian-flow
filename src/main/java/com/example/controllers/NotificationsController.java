package com.example.controllers;

import com.example.models.Notification;
import com.example.models.User;
import com.example.models.Task;
import com.example.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;

public class NotificationsController {

    @FXML private Button dashboardBtn;
    @FXML private Button myTasksBtn;
    @FXML private Button myProjectsBtn;
    @FXML private Button notificationsBtn;
    @FXML private Button profileBtn;
    @FXML private Button logoutBtn;
    
    @FXML private Label pageTitle;
    @FXML private Label unreadCountLabel;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private Button markAllReadBtn;
    @FXML private Button refreshBtn;
    @FXML private ListView<HBox> notificationsListView;
    @FXML private ToggleGroup viewToggle;
    @FXML private RadioButton allRadio;
    @FXML private RadioButton unreadRadio;
    
    private User currentUser;
    private ObservableList<Notification> allNotifications = FXCollections.observableArrayList();
    private ObservableList<Notification> filteredNotifications = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        currentUser = SessionManager.getCurrentUser();
        
        if (currentUser == null) {
            System.err.println("❌ No user in session!");
            return;
        }
        
        System.out.println("✅ NotificationsController initialized for: " + currentUser.getFullName());
        
        setupUI();
        loadSampleNotifications();
        setupFilters();
        setupNavigation();
        updateUnreadCount();
    }
    
    private void setupUI() {
        pageTitle.setText("Notifications - " + currentUser.getFullName());
        
        // Setup filter combo box
        filterComboBox.setItems(FXCollections.observableArrayList(
            "All Types", "Mentions", "Replies", "Task Assignments", "Deadlines", "Status Changes"
        ));
        filterComboBox.setValue("All Types");
        
        // Setup view toggle
        allRadio.setSelected(true);
        
        // Setup list view cell factory
        notificationsListView.setCellFactory(lv -> new ListCell<HBox>() {
            @Override
            protected void updateItem(HBox item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(item);
                }
            }
        });
    }
    
    private void loadSampleNotifications() {
        allNotifications.clear();
        
        // Add sample notifications
        Notification n1 = new Notification(currentUser.getId(), "mention", "🔔 You were mentioned",
            "@sarah mentioned you in a comment", "Implement Login API", 101);
        n1.setCreatedAt(LocalDateTime.now().minusMinutes(25));
        
        Notification n2 = new Notification(currentUser.getId(), "reply", "💬 New reply",
            "Mike replied to your comment", "Fix Navigation Bug", 102);
        n2.setCreatedAt(LocalDateTime.now().minusHours(2));
        
        Notification n3 = new Notification(currentUser.getId(), "task_assigned", "📋 Task assigned",
            "You have been assigned a new task", "Code Review", 103);
        n3.setCreatedAt(LocalDateTime.now().minusHours(5));
        
        Notification n4 = new Notification(currentUser.getId(), "deadline", "⏰ Deadline approaching",
            "Task 'Implement Login API' is due tomorrow", "Implement Login API", 101);
        n4.setCreatedAt(LocalDateTime.now().minusHours(8));
        
        Notification n5 = new Notification(currentUser.getId(), "status_change", "🔄 Status changed",
            "Task 'Update Documentation' moved to Done", "Update Documentation", 104);
        n5.setCreatedAt(LocalDateTime.now().minusDays(1));
        
        Notification n6 = new Notification(currentUser.getId(), "mention", "🔔 You were mentioned",
            "@john - Sarah mentioned you in project meeting", "Mobile App Project", 201);
        n6.setCreatedAt(LocalDateTime.now().minusDays(2));
        
        Notification n7 = new Notification(currentUser.getId(), "reply", "💬 New reply",
            "Alice replied to your comment on 'Write Unit Tests'", "Write Unit Tests", 105);
        n7.setCreatedAt(LocalDateTime.now().minusDays(3));
        
        Notification n8 = new Notification(currentUser.getId(), "task_assigned", "📋 Task assigned",
            "You have been assigned to review PR", "Code Review", 103);
        n8.setCreatedAt(LocalDateTime.now().minusDays(4));
        
        allNotifications.addAll(n1, n2, n3, n4, n5, n6, n7, n8);
        
        // Sort by date (newest first)
        allNotifications.sort(Comparator.comparing(Notification::getCreatedAt).reversed());
        
        refreshNotificationsList();
    }
    
    private void refreshNotificationsList() {
        notificationsListView.getItems().clear();
        
        for (Notification notification : filteredNotifications.isEmpty() ? allNotifications : filteredNotifications) {
            HBox item = createNotificationItem(notification);
            notificationsListView.getItems().add(item);
        }
    }
    
    private HBox createNotificationItem(Notification notification) {
        HBox item = new HBox(15);
        item.setPadding(new Insets(15));
        item.setStyle("-fx-background-color: " + (notification.isRead() ? "white" : "#f0f9ff") + 
                     "; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e2e8f0; -fx-border-width: 1;");
        item.setPrefWidth(700);
        
        // Icon with color
        Label iconLabel = new Label(notification.getIcon());
        iconLabel.setStyle("-fx-font-size: 24px; -fx-min-width: 40; -fx-alignment: center;");
        
        // Content VBox
        VBox contentBox = new VBox(5);
        contentBox.setPrefWidth(450);
        
        // Title and time
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label(notification.getTitle());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label timeLabel = new Label(notification.getTimeAgo());
        timeLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
        
        if (!notification.isRead()) {
            Label unreadDot = new Label("●");
            unreadDot.setStyle("-fx-text-fill: #3b82f6; -fx-font-size: 10px;");
            headerBox.getChildren().addAll(titleLabel, unreadDot, timeLabel);
        } else {
            headerBox.getChildren().addAll(titleLabel, timeLabel);
        }
        
        // Message
        Label messageLabel = new Label(notification.getMessage());
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-text-fill: #334155; -fx-font-size: 13px;");
        
        // Related item
        Label relatedLabel = new Label("📎 " + notification.getRelatedItem());
        relatedLabel.setStyle("-fx-text-fill: #6366f1; -fx-font-size: 12px; -fx-background-color: #e0e7ff; " +
                             "-fx-padding: 2 8; -fx-background-radius: 12;");
        
        contentBox.getChildren().addAll(headerBox, messageLabel, relatedLabel);
        
        // Actions
        VBox actionBox = new VBox(10);
        actionBox.setAlignment(Pos.CENTER);
        
        if (!notification.isRead()) {
            Button markReadBtn = new Button("✓ Mark read");
            markReadBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748b; -fx-font-size: 11px; -fx-cursor: hand;");
            markReadBtn.setOnAction(e -> {
                notification.setRead(true);
                refreshNotificationsList();
                updateUnreadCount();
            });
            actionBox.getChildren().add(markReadBtn);
        }
        
        if (notification.isActionable()) {
            Button viewBtn = new Button("View →");
            viewBtn.setStyle("-fx-background-color: #6366f1; -fx-text-fill: white; -fx-padding: 5 15; -fx-background-radius: 5; -fx-font-size: 11px; -fx-cursor: hand;");
            viewBtn.setOnAction(e -> handleNotificationAction(notification));
            actionBox.getChildren().add(viewBtn);
        }
        
        HBox.setHgrow(contentBox, Priority.ALWAYS);
        item.getChildren().addAll(iconLabel, contentBox, actionBox);
        
        return item;
    }
    
    private void setupFilters() {
        // Filter by type
        filterComboBox.setOnAction(e -> applyFilters());
        
        // Toggle between all and unread
        allRadio.setOnAction(e -> applyFilters());
        unreadRadio.setOnAction(e -> applyFilters());
        
        // Mark all as read
        markAllReadBtn.setOnAction(e -> {
            for (Notification n : allNotifications) {
                n.setRead(true);
            }
            refreshNotificationsList();
            updateUnreadCount();
            showInfo("Success", "All notifications marked as read");
        });
        
        // Refresh
        refreshBtn.setOnAction(e -> {
            refreshNotificationsList();
            showInfo("Refreshed", "Notifications list updated");
        });
    }
    
    private void applyFilters() {
        filteredNotifications.clear();
        
        String typeFilter = filterComboBox.getValue();
        boolean showOnlyUnread = unreadRadio.isSelected();
        
        for (Notification n : allNotifications) {
            boolean matchesType = true;
            if (!"All Types".equals(typeFilter)) {
                switch(typeFilter) {
                    case "Mentions":
                        matchesType = "mention".equals(n.getType());
                        break;
                    case "Replies":
                        matchesType = "reply".equals(n.getType());
                        break;
                    case "Task Assignments":
                        matchesType = "task_assigned".equals(n.getType());
                        break;
                    case "Deadlines":
                        matchesType = "deadline".equals(n.getType());
                        break;
                    case "Status Changes":
                        matchesType = "status_change".equals(n.getType());
                        break;
                }
            }
            
            boolean matchesRead = !showOnlyUnread || !n.isRead();
            
            if (matchesType && matchesRead) {
                filteredNotifications.add(n);
            }
        }
        
        refreshNotificationsList();
    }
    
    private void handleNotificationAction(Notification notification) {
        System.out.println("🔍 Handling notification: " + notification.getTitle());
        
        switch(notification.getActionType()) {
            case "view_task":
                openTask(notification.getRelatedId(), notification.getRelatedItem());
                break;
            case "view_project":
                openProject(notification.getRelatedId(), notification.getRelatedItem());
                break;
            default:
                showInfo("Info", "Action not implemented yet");
        }
        
        // Mark as read when viewed
        if (!notification.isRead()) {
            notification.setRead(true);
            refreshNotificationsList();
            updateUnreadCount();
        }
    }
    
    private void openTask(int taskId, String taskName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fxml/TaskDetail.fxml"));
            Parent root = loader.load();
            
            // Create a dummy task for demo
            Task task = new Task(taskName, "High", "In Progress", "Mobile App", LocalDateTime.now().toLocalDate());
            
            TaskDetailController controller = loader.getController();
            controller.setTask(task);
            
            Stage stage = new Stage();
            stage.setTitle("Task Details - " + taskName);
            stage.setScene(new Scene(root));
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open task");
        }
    }
    
    private void openProject(int projectId, String projectName) {
        showInfo("Project", "Opening project: " + projectName);
        // Implement project view
    }
    
    private void updateUnreadCount() {
        long unread = allNotifications.stream().filter(n -> !n.isRead()).count();
        unreadCountLabel.setText(unread + " unread");
        
        // Update badge on notifications button if needed
        if (unread > 0) {
            notificationsBtn.setText("🔔 Notifications (" + unread + ")");
        } else {
            notificationsBtn.setText("🔔 Notifications");
        }
    }
    
    private void setupNavigation() {
        dashboardBtn.setOnAction(e -> loadPage("member-dashboard"));
        myTasksBtn.setOnAction(e -> loadPage("member-dashboard"));
        myProjectsBtn.setOnAction(e -> loadPage("member-dashboard"));
        profileBtn.setOnAction(e -> loadPage("member-dashboard"));
        logoutBtn.setOnAction(e -> handleLogout());
    }
    
    private void loadPage(String page) {
        try {
            String fxmlFile = "/com/example/fxml/MemberDashboard.fxml";
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) dashboardBtn.getScene().getWindow();
            stage.setScene(new Scene(root,1200,800));
            stage.setTitle("Task Manager - Member Dashboard");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load dashboard");
        }
    }
    
    private void handleLogout() {
        try {
            SessionManager.clearSession();
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