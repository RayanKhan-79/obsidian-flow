package com.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;

import com.example.models.Task;

public class AddTaskController {

    @FXML private TextField taskNameField;
    @FXML private ComboBox<String> assignedBox;
    @FXML private ComboBox<String> priorityBox;
    @FXML private ComboBox<String> statusBox;
    @FXML private DatePicker deadlinePicker;
    @FXML private Button cancelButton;

    @FXML
    public void initialize() {
        // Initialize combo boxes
        assignedBox.getItems().addAll("Ali", "Sara", "John", "Ahmed", "Mike", "Sarah");
        assignedBox.setValue("John"); // Default value
        
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
        
        // Create new task
        Task task = new Task(
                taskNameField.getText().trim(),
                assignedBox.getValue(),
                priorityBox.getValue(),
                statusBox.getValue(),
                deadlinePicker.getValue()
        );
        
        // Add to shared tasks list
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