package com.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import com.example.models.Task;

public class AddTaskController {

    @FXML private TextField taskNameField;
    @FXML private ComboBox<String> assignedBox;
    @FXML private ComboBox<String> priorityBox;
    @FXML private ComboBox<String> statusBox;
    @FXML private DatePicker deadlinePicker;

    @FXML
    public void initialize() {

        assignedBox.getItems().addAll("Ali", "Sara", "John", "Ahmed");

        priorityBox.getItems().addAll("Low","Medium","High");

        statusBox.getItems().addAll("Todo","In Progress","Completed");

    }

    @FXML
    private void addTask() {

        Task task = new Task(
                taskNameField.getText(),
                assignedBox.getValue(),
                priorityBox.getValue(),
                statusBox.getValue(),
                deadlinePicker.getValue()
        );

        TasksController.getTasks().add(task);

        taskNameField.getScene().getWindow().hide();

    }

}