package com.example.controllers;

import com.example.models.Task;
import com.example.models.Comment;
import com.example.models.User;
import com.example.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskDetailController {

    @FXML private Label taskProjectLabel;
    @FXML private Label taskIdLabel;
    @FXML private Label taskTitleLabel;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private ComboBox<String> priorityComboBox;
    @FXML private Label assigneeLabel;
    @FXML private Label dueDateLabel;
    @FXML private TextArea descriptionArea;
    @FXML private TextArea newCommentArea;
    @FXML private Button postCommentButton;
    @FXML private Button cancelCommentButton;
    @FXML private ListView<Comment> commentsListView;
    @FXML private Label createdByLabel;
    @FXML private Label createdAtLabel;
    @FXML private Button backButton;
    @FXML private Button refreshButton;
    @FXML private Label commentCountLabel;
    @FXML private ComboBox<String> sortCommentsCombo;
    
    private Task currentTask;
    private List<Comment> comments = new ArrayList<>();
    private User currentUser;
    private Comment selectedCommentForReply;

    @FXML
    public void initialize() {
        currentUser = SessionManager.getCurrentUser();
        
        setupComboBoxes();
        setupEventHandlers();
        setupCommentsListView();
    }
    
    private void setupComboBoxes() {
        statusComboBox.setItems(javafx.collections.FXCollections.observableArrayList(
            "To Do", "In Progress", "Done", "Blocked"
        ));
        
        priorityComboBox.setItems(javafx.collections.FXCollections.observableArrayList(
            "High", "Medium", "Low"
        ));
        
        sortCommentsCombo.setItems(javafx.collections.FXCollections.observableArrayList(
            "Newest First", "Oldest First"
        ));
        sortCommentsCombo.setValue("Newest First");
    }
    
    private void setupEventHandlers() {
        postCommentButton.setOnAction(e -> handlePostComment());
        cancelCommentButton.setOnAction(e -> {
            newCommentArea.clear();
            selectedCommentForReply = null;
            postCommentButton.setText("Post Comment");
        });
        backButton.setOnAction(e -> handleClose());
        refreshButton.setOnAction(e -> refreshComments());
        sortCommentsCombo.setOnAction(e -> refreshCommentsList());
        
        statusComboBox.setOnAction(e -> handleStatusChange());
        priorityComboBox.setOnAction(e -> handlePriorityChange());
    }
    
    private void setupCommentsListView() {
        commentsListView.setCellFactory(lv -> new ListCell<Comment>() {
            @Override
            protected void updateItem(Comment comment, boolean empty) {
                super.updateItem(comment, empty);
                if (empty || comment == null) {
                    setGraphic(null);
                } else {
                    VBox commentBox = createCommentBox(comment, 0);
                    setGraphic(commentBox);
                }
            }
        });
    }
    
    private VBox createCommentBox(Comment comment, int indentLevel) {
        VBox container = new VBox(10);
        container.setPadding(new Insets(10, 10, 10, 10 + (indentLevel * 30)));
        container.setStyle("-fx-background-color: " + (indentLevel == 0 ? "#f8fafc" : "#ffffff") + 
                          "; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e2e8f0; -fx-border-width: 1;");
        
        // Main comment content
        HBox mainContent = new HBox(12);
        
        // Avatar with initials
        Label avatarLabel = new Label(comment.getInitials());
        avatarLabel.setStyle("-fx-background-color: #6366f1; -fx-text-fill: white; -fx-font-weight: bold; " +
                           "-fx-min-width: 36; -fx-min-height: 36; -fx-max-width: 36; -fx-max-height: 36; " +
                           "-fx-background-radius: 50; -fx-alignment: center; -fx-font-size: 14px;");
        
        // Content VBox
        VBox contentBox = new VBox(5);
        contentBox.setPrefWidth(500);
        
        // Header with user info and time
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label nameLabel = new Label(comment.getUserName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label roleLabel = new Label(getUserRole(comment.getUserId()));
        roleLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px; -fx-background-color: #e2e8f0; " +
                          "-fx-padding: 2 8; -fx-background-radius: 12;");
        
        Label timeLabel = new Label(comment.getFormattedTime());
        timeLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
        
        if (comment.isEdited()) {
            Label editedLabel = new Label("(edited)");
            editedLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px; -fx-font-style: italic;");
            headerBox.getChildren().addAll(nameLabel, roleLabel, timeLabel, editedLabel);
        } else {
            headerBox.getChildren().addAll(nameLabel, roleLabel, timeLabel);
        }
        
        // Comment text with mention highlighting
        Label textLabel = new Label(comment.getContent());
        textLabel.setWrapText(true);
        textLabel.setStyle("-fx-line-spacing: 2; -fx-font-size: 13px;");
        
        // Action buttons
        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER_LEFT);
        actionBox.setPadding(new Insets(5, 0, 0, 0));
        
        // Like button
        Button likeBtn = new Button("👍 " + comment.getLikeCount());
        likeBtn.setStyle("-fx-background-color: " + (comment.isLikedBy(currentUser.getId()) ? "#6366f1" : "#f1f5f9") + 
                        "; -fx-text-fill: " + (comment.isLikedBy(currentUser.getId()) ? "white" : "#334155") + 
                        "; -fx-padding: 3 10; -fx-background-radius: 15; -fx-cursor: hand; -fx-font-size: 11px;");
        likeBtn.setOnAction(e -> handleLikeComment(comment, likeBtn));
        
        // Reply button
        Button replyBtn = new Button("↩️ Reply");
        replyBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #6366f1; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 3 8;");
        replyBtn.setOnAction(e -> handleReplyToComment(comment));
        
        actionBox.getChildren().addAll(likeBtn, replyBtn);
        
        // Edit/Delete buttons (only for user's own comments)
        if (currentUser != null && (comment.getUserId() == currentUser.getId())) {
            Button editBtn = new Button("✏️ Edit");
            editBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748b; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 3 8;");
            editBtn.setOnAction(e -> handleEditComment(comment));
            
            Button deleteBtn = new Button("🗑️ Delete");
            deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 3 8;");
            deleteBtn.setOnAction(e -> handleDeleteComment(comment));
            
            actionBox.getChildren().addAll(editBtn, deleteBtn);
        }
        
        contentBox.getChildren().addAll(headerBox, textLabel, actionBox);
        mainContent.getChildren().addAll(avatarLabel, contentBox);
        
        container.getChildren().add(mainContent);
        
        // Add replies
        if (!comment.getReplies().isEmpty()) {
            VBox repliesBox = new VBox(10);
            repliesBox.setPadding(new Insets(10, 0, 0, 20));
            
            for (Comment reply : comment.getReplies()) {
                VBox replyBox = createCommentBox(reply, indentLevel + 1);
                repliesBox.getChildren().add(replyBox);
            }
            
            container.getChildren().add(repliesBox);
        }
        
        return container;
    }
    
    private String getUserRole(int userId) {
        // In real app, get from user service
        if (userId == 1) return "Project Manager";
        if (userId == 2) return "You";
        return "Team Member";
    }
    
    public void setTask(Task task) {
        this.currentTask = task;
        
        // Populate fields
        taskTitleLabel.setText(task.getName() != null ? task.getName() : "Untitled Task");
        taskProjectLabel.setText(task.getProject() != null ? task.getProject() : "No Project");
        taskIdLabel.setText("TASK-" + (int)(Math.random() * 1000));
        statusComboBox.setValue(task.getStatus() != null ? task.getStatus() : "To Do");
        priorityComboBox.setValue(task.getPriority() != null ? task.getPriority() : "Medium");
        assigneeLabel.setText(task.getAssignedTo() != null ? task.getAssignedTo() : "Unassigned");
        dueDateLabel.setText(task.getDeadline() != null ? task.getDeadline().toString() : "No deadline");
        descriptionArea.setText(task.getDescription() != null ? task.getDescription() : 
                              "No description provided for " + task.getName());
        
        createdByLabel.setText(task.getCreatedBy() != null ? task.getCreatedBy() : "System");
        createdAtLabel.setText(task.getCreatedAt() != null ? 
                              task.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")) : 
                              "Unknown");
        
        // Load comments
        loadSampleComments();
    }
    
    private void loadSampleComments() {
        comments.clear();
        
        // Main comment 1
        Comment comment1 = new Comment(currentTask.getId(), 1, "Sarah Johnson", 
            "@john I've started working on the login API. The authentication flow needs to be discussed.");
        comment1.setId(1);
        comment1.setCreatedAt(LocalDateTime.now().minusHours(3));
        comment1.addLike(2);
        comment1.addLike(3);
        
        // Reply to comment1
        Comment reply1 = new Comment(currentTask.getId(), 2, "John Doe", 
            "@sarah Sure! I think we should use JWT tokens. What's your opinion?");
        reply1.setId(2);
        reply1.setParentCommentId(1);
        reply1.setCreatedAt(LocalDateTime.now().minusHours(2));
        reply1.addLike(1);
        
        // Another reply
        Comment reply2 = new Comment(currentTask.getId(), 1, "Sarah Johnson", 
            "@john Agreed. JWT is the way to go. I'll update the documentation.");
        reply2.setId(3);
        reply2.setParentCommentId(1);
        reply2.setCreatedAt(LocalDateTime.now().minusHours(1));
        
        comment1.addReply(reply1);
        comment1.addReply(reply2);
        
        // Main comment 2
        Comment comment2 = new Comment(currentTask.getId(), 3, "Mike Chen", 
            "@john @sarah I've already implemented the JWT helper class. Let me know if you need it.");
        comment2.setId(4);
        comment2.setCreatedAt(LocalDateTime.now().minusDays(1));
        comment2.addMention(1);
        comment2.addMention(2);
        comment2.addLike(2);
        
        // Main comment 3
        Comment comment3 = new Comment(currentTask.getId(), 4, "Alice Brown", 
            "Don't forget to add rate limiting to the login endpoint.");
        comment3.setId(5);
        comment3.setCreatedAt(LocalDateTime.now().minusDays(2));
        comment3.addLike(1);
        comment3.addLike(2);
        comment3.addLike(3);
        
        comments.add(comment1);
        comments.add(comment2);
        comments.add(comment3);
        
        refreshCommentsList();
        updateCommentCount();
    }
    
    private void refreshCommentsList() {
        commentsListView.getItems().clear();
        
        // Sort comments
        List<Comment> sortedComments = new ArrayList<>(comments);
        if ("Newest First".equals(sortCommentsCombo.getValue())) {
            sortedComments.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        } else {
            sortedComments.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
        }
        
        commentsListView.getItems().addAll(sortedComments);
    }
    
    private void updateCommentCount() {
        int totalComments = comments.size();
        int totalReplies = comments.stream().mapToInt(c -> c.getReplies().size()).sum();
        commentCountLabel.setText("Comments (" + (totalComments + totalReplies) + ")");
    }
    
    @FXML
    private void handlePostComment() {
        String content = newCommentArea.getText();
        if (content == null || content.trim().isEmpty()) {
            showAlert("Error", "Comment cannot be empty");
            return;
        }
        
        if (selectedCommentForReply != null) {
            // This is a reply
            Comment reply = new Comment(
                currentTask.getId(),
                currentUser.getId(),
                currentUser.getFullName(),
                content
            );
            reply.setParentCommentId(selectedCommentForReply.getId());
            reply.setCreatedAt(LocalDateTime.now());
            reply.setId(comments.size() + 100);
            
            // Find parent and add reply
            for (Comment c : comments) {
                if (c.getId() == selectedCommentForReply.getId()) {
                    c.addReply(reply);
                    break;
                }
            }
            
            showInfo("Success", "Reply posted");
            selectedCommentForReply = null;
            postCommentButton.setText("Post Comment");
        } else {
            // This is a new top-level comment
            Comment newComment = new Comment(
                currentTask.getId(),
                currentUser.getId(),
                currentUser.getFullName(),
                content
            );
            newComment.setCreatedAt(LocalDateTime.now());
            newComment.setId(comments.size() + 100);
            
            comments.add(newComment);
            showInfo("Success", "Comment posted");
        }
        
        newCommentArea.clear();
        refreshCommentsList();
        updateCommentCount();
    }
    
    private void handleReplyToComment(Comment comment) {
        selectedCommentForReply = comment;
        newCommentArea.requestFocus();
        newCommentArea.setPromptText("Reply to " + comment.getUserName() + "...");
        postCommentButton.setText("Post Reply");
    }
    
    private void handleLikeComment(Comment comment, Button likeBtn) {
        if (comment.isLikedBy(currentUser.getId())) {
            comment.removeLike(currentUser.getId());
        } else {
            comment.addLike(currentUser.getId());
        }
        
        likeBtn.setText("👍 " + comment.getLikeCount());
        likeBtn.setStyle("-fx-background-color: " + (comment.isLikedBy(currentUser.getId()) ? "#6366f1" : "#f1f5f9") + 
                        "; -fx-text-fill: " + (comment.isLikedBy(currentUser.getId()) ? "white" : "#334155") + 
                        "; -fx-padding: 3 10; -fx-background-radius: 15; -fx-cursor: hand; -fx-font-size: 11px;");
    }
    
    private void handleEditComment(Comment comment) {
        TextInputDialog dialog = new TextInputDialog(comment.getContent());
        dialog.setTitle("Edit Comment");
        dialog.setHeaderText("Edit your comment");
        dialog.setContentText("Content:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(content -> {
            if (!content.trim().isEmpty()) {
                comment.setContent(content);
                refreshCommentsList();
                showInfo("Success", "Comment updated");
            }
        });
    }
    
    private void handleDeleteComment(Comment comment) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Comment");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this comment?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (comment.getParentCommentId() == null) {
                    // Top-level comment
                    comments.remove(comment);
                } else {
                    // Reply - find parent and remove
                    for (Comment c : comments) {
                        c.getReplies().removeIf(r -> r.getId() == comment.getId());
                    }
                }
                refreshCommentsList();
                updateCommentCount();
                showInfo("Success", "Comment deleted");
            }
        });
    }
    
    private void handleStatusChange() {
        String newStatus = statusComboBox.getValue();
        String oldStatus = currentTask.getStatus();
        
        if (!newStatus.equals(oldStatus)) {
            currentTask.setStatus(newStatus);
            
            // Add system comment about status change
            Comment statusComment = new Comment(
                currentTask.getId(),
                0,
                "System",
                "🔄 changed status from **" + oldStatus + "** to **" + newStatus + "**"
            );
            statusComment.setCreatedAt(LocalDateTime.now());
            comments.add(0, statusComment);
            refreshCommentsList();
            updateCommentCount();
            
            showInfo("Success", "Status updated to " + newStatus);
        }
    }
    
    private void handlePriorityChange() {
        String newPriority = priorityComboBox.getValue();
        String oldPriority = currentTask.getPriority();
        
        if (!newPriority.equals(oldPriority)) {
            currentTask.setPriority(newPriority);
            
            // Add system comment about priority change
            Comment priorityComment = new Comment(
                currentTask.getId(),
                0,
                "System",
                "🎯 changed priority from **" + oldPriority + "** to **" + newPriority + "**"
            );
            priorityComment.setCreatedAt(LocalDateTime.now());
            comments.add(0, priorityComment);
            refreshCommentsList();
            updateCommentCount();
        }
    }
    
    private void refreshComments() {
        refreshCommentsList();
        showInfo("Refreshed", "Comments list updated");
    }
    
    @FXML
    private void handleClose() {
        Stage stage = (Stage) taskTitleLabel.getScene().getWindow();
        stage.close();
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