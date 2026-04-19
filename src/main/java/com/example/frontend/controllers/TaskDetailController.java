package com.example.frontend.controllers;

import com.example.frontend.models.Task;
import com.example.frontend.models.Comment;
import com.example.frontend.models.User;
import com.example.frontend.utils.DatabaseUtil;
import com.example.frontend.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
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
    @FXML private ComboBox<String> assigneeComboBox;
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
        if (statusComboBox != null) {
            statusComboBox.setItems(javafx.collections.FXCollections.observableArrayList(
                "To Do", "In Progress", "Done", "Blocked"
            ));
        }
        
        if (priorityComboBox != null) {
            priorityComboBox.setItems(javafx.collections.FXCollections.observableArrayList(
                "High", "Medium", "Low"
            ));
        }
        
        if (sortCommentsCombo != null) {
            sortCommentsCombo.setItems(javafx.collections.FXCollections.observableArrayList(
                "Newest First", "Oldest First"
            ));
            sortCommentsCombo.setValue("Newest First");
        }

        if (assigneeComboBox != null) {
            assigneeComboBox.getItems().setAll(
                DatabaseUtil.getAllUsers().stream().map(User::getFullName).toList()
            );
        }
    }
    
    private void setupEventHandlers() {
        if (postCommentButton != null) {
            postCommentButton.setOnAction(e -> handlePostComment());
        }
        if (cancelCommentButton != null) {
            cancelCommentButton.setOnAction(e -> {
                if (newCommentArea != null) {
                    newCommentArea.clear();
                }
                selectedCommentForReply = null;
                if (postCommentButton != null) {
                    postCommentButton.setText("Post Comment");
                }
            });
        }
        if (backButton != null) {
            backButton.setOnAction(e -> handleClose());
        }
        if (refreshButton != null) {
            refreshButton.setOnAction(e -> refreshComments());
        }
        if (sortCommentsCombo != null) {
            sortCommentsCombo.setOnAction(e -> refreshCommentsList());
        }
        if (assigneeComboBox != null) {
            assigneeComboBox.setOnAction(e -> handleAssigneeChange());
        }
        
        if (statusComboBox != null) {
            statusComboBox.setOnAction(e -> handleStatusChange());
        }
        if (priorityComboBox != null) {
            priorityComboBox.setOnAction(e -> handlePriorityChange());
        }
    }
    
    private void setupCommentsListView() {
        if (commentsListView == null) {
            return;
        }

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
        if (taskTitleLabel != null) taskTitleLabel.setText(task.getName() != null ? task.getName() : "Untitled Task");
        if (taskProjectLabel != null) taskProjectLabel.setText(task.getProject() != null ? task.getProject() : "No Project");
        if (taskIdLabel != null) taskIdLabel.setText("TASK-" + (int)(Math.random() * 1000));
        if (statusComboBox != null) statusComboBox.setValue(task.getStatus() != null ? task.getStatus() : "To Do");
        if (priorityComboBox != null) priorityComboBox.setValue(task.getPriority() != null ? task.getPriority() : "Medium");
        if (assigneeLabel != null) assigneeLabel.setText(task.getAssignedTo() != null ? task.getAssignedTo() : "Unassigned");
        if (assigneeComboBox != null) {
            assigneeComboBox.setValue(task.getAssignedTo());
        }
        if (dueDateLabel != null) dueDateLabel.setText(task.getDeadline() != null ? task.getDeadline().toString() : "No deadline");
        if (descriptionArea != null) {
            descriptionArea.setText(task.getDescription() != null ? task.getDescription() : 
                                "No description provided for " + task.getName());
        }
        
        if (createdByLabel != null) createdByLabel.setText(task.getCreatedBy() != null ? task.getCreatedBy() : "System");
        if (createdAtLabel != null) createdAtLabel.setText(task.getCreatedAt() != null ? 
                              task.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")) : 
                              "Unknown");
        
        // Load persisted comments for this task.
        loadComments();
    }
    
    private void loadComments() {
        comments.clear();
        if (currentTask != null) {
            comments.addAll(DatabaseUtil.getCommentsForTask(currentTask.getId()));
        }
        refreshCommentsList();
        updateCommentCount();
    }
    
    private void refreshCommentsList() {
        if (commentsListView == null) {
            return;
        }

        commentsListView.getItems().clear();
        
        // Sort comments
        List<Comment> sortedComments = new ArrayList<>(comments);
        if (sortCommentsCombo == null || "Newest First".equals(sortCommentsCombo.getValue())) {
            sortedComments.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        } else {
            sortedComments.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
        }
        
        commentsListView.getItems().addAll(sortedComments);
    }
    
    private void updateCommentCount() {
        if (commentCountLabel == null) {
            return;
        }

        int totalComments = comments.size();
        int totalReplies = comments.stream().mapToInt(c -> c.getReplies().size()).sum();
        commentCountLabel.setText("Comments (" + (totalComments + totalReplies) + ")");
    }
    
    @FXML
    private void handlePostComment() {
        if (newCommentArea == null || currentUser == null || currentTask == null) {
            return;
        }

        String content = newCommentArea.getText();
        if (content == null || content.trim().isEmpty()) {
            showAlert("Error", "Comment cannot be empty");
            return;
        }

        String finalContent = content.trim();
        if (selectedCommentForReply != null) {
            finalContent = "@" + selectedCommentForReply.getUserName() + " " + finalContent;
        }

        var saved = DatabaseUtil.addCommentToTask(currentTask.getId(), finalContent);
        if (saved.isEmpty()) {
            showAlert("Error", "Could not save comment");
            return;
        }
        
        if (selectedCommentForReply != null) {
            showInfo("Success", "Reply posted");
            selectedCommentForReply = null;
            postCommentButton.setText("Post Comment");
            newCommentArea.setPromptText("Write a comment...");
        } else {
            showInfo("Success", "Comment posted");
        }
        
        newCommentArea.clear();
        loadComments();
    }
    
    private void handleReplyToComment(Comment comment) {
        selectedCommentForReply = comment;
        if (newCommentArea != null) {
            newCommentArea.requestFocus();
            newCommentArea.setPromptText("Reply to " + comment.getUserName() + "...");
        }
        if (postCommentButton != null) {
            postCommentButton.setText("Post Reply");
        }
    }
    
    private void handleLikeComment(Comment comment, Button likeBtn) {
        if (currentUser == null) {
            return;
        }

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
        if (currentTask == null || statusComboBox.getValue() == null)
            return;

        String newStatus = statusComboBox.getValue();
        String oldStatus = currentTask.getStatus();
        
        if (!newStatus.equals(oldStatus)) {
            currentTask.setStatus(newStatus);
            persistTaskChanges();
            
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
        if (currentTask == null || priorityComboBox.getValue() == null)
            return;

        String newPriority = priorityComboBox.getValue();
        String oldPriority = currentTask.getPriority();
        
        if (!newPriority.equals(oldPriority)) {
            currentTask.setPriority(newPriority);
            persistTaskChanges();
            
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
        loadComments();
        showInfo("Refreshed", "Comments list updated");
    }

    private void handleAssigneeChange() {
        if (currentTask == null || assigneeComboBox == null)
            return;

        String assignee = assigneeComboBox.getValue();
        if (assignee == null || assignee.isBlank())
            return;

        User user = DatabaseUtil.findUserByFullName(assignee);
        currentTask.setAssignedTo(assignee);
        currentTask.setAssignedToId(user == null ? 0 : user.getId());
        if (assigneeLabel != null) {
            assigneeLabel.setText(assignee);
        }
        persistTaskChanges();
    }

    private void persistTaskChanges() {
        if (currentTask == null)
            return;

        if (descriptionArea != null) {
            currentTask.setDescription(descriptionArea.getText());
        }
        boolean updated = DatabaseUtil.updateTask(currentTask);
        if (!updated) {
            showAlert("Update Failed", "Could not save task changes");
        }
    }
    
    @FXML
    private void handleClose() {
        persistTaskChanges();
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