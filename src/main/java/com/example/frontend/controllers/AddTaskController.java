package com.example.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.List;

import com.example.frontend.models.Project;
import com.example.frontend.models.Task;
import com.example.frontend.utils.DatabaseUtil;


public class AddTaskController {

    @FXML private TextField taskNameField;
    @FXML private ComboBox<String> assignedBox;
    @FXML private ComboBox<String> priorityBox;
    @FXML private ComboBox<String> statusBox;
    @FXML private DatePicker deadlinePicker;
    @FXML private Button cancelButton;

    @FXML
    public void initialize() {
        List<String> userNames = DatabaseUtil.getAllUsers().stream()
            .map(user -> user.getFullName() == null || user.getFullName().isBlank() ? user.getUsername() : user.getFullName())
            .toList();
        assignedBox.getItems().addAll(userNames);
        if (!assignedBox.getItems().isEmpty()) {
            assignedBox.setValue(assignedBox.getItems().get(0));
        }
        
        priorityBox.getItems().addAll("Low", "Medium", "High");
        priorityBox.setValue("Medium"); // Default value
        
        statusBox.getItems().addAll("To Do", "In Progress", "Done", "Blocked");
        statusBox.setValue("To Do"); // Default value
        
        // Set default deadline to tomorrow
        deadlinePicker.setValue(LocalDate.now().plusDays(1));
    }

    @FXML
    private void addTask() {
        // Validate input
        if (taskNameField.getText() == null || taskNameField.getText().trim().isEmpty()) {
            showAlert("Error", "Task name is required");
            return;
        }
        
        List<Project> projects = DatabaseUtil.getCurrentUserProjects();
        if (projects.isEmpty()) {
            showAlert("Error", "No accessible project found. Create or join a project first.");
            return;
        }

        Project selectedProject = projects.get(0);
        var selectedUser = DatabaseUtil.findUserByFullName(assignedBox.getValue());
        var created = DatabaseUtil.createTask(
            selectedProject.getId(),
            taskNameField.getText().trim(),
            "",
            priorityBox.getValue(),
            deadlinePicker.getValue(),
            selectedUser == null ? null : selectedUser.getId()
        );

        if (created.isEmpty()) {
            showAlert("Error", "Task could not be created. Ensure you have project access.");
            return;
        }

        Task task = created.get();
        task.setAssignedTo(assignedBox.getValue());
        task.setAssignedToId(selectedUser == null ? 0 : selectedUser.getId());
        TasksController.getTasks().add(task);
        
        // Show success message
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Task added successfully!");
        alert.showAndWait();
        
        // Close window
        Stage stage = (Stage) taskNameField.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) taskNameField.getScene().getWindow();
        stage.close();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}