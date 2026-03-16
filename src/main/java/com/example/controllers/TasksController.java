package com.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.example.models.Task;

public class TasksController {

    @FXML private TableView<Task> taskTable;

    @FXML private TableColumn<Task,String> taskNameCol;
    @FXML private TableColumn<Task,String> assignedCol;
    @FXML private TableColumn<Task,String> priorityCol;
    @FXML private TableColumn<Task,String> statusCol;
    @FXML private TableColumn<Task,String> deadlineCol;

    private static ObservableList<Task> tasks = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        taskNameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        assignedCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAssignedTo()));
        priorityCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPriority()));
        statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus()));
        deadlineCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDeadline().toString()));

        taskTable.setItems(tasks);

    }

    @FXML
    private void openAddTaskDialog() {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fxml/addTask.fxml"));

            Stage stage = new Stage();
            stage.setTitle("Add Task");

            stage.setScene(new Scene(loader.load()));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static ObservableList<Task> getTasks() {
        return tasks;
    }

}