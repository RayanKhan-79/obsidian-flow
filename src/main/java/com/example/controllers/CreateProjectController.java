package com.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CreateProjectController {

    @FXML private TextField projectNameField;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ListView<CheckBox> teamMembersListView;
    @FXML private ComboBox<String> colorComboBox;
    @FXML private Button cancelButton;
    @FXML private Button createButton;
    
    private ObservableList<Project> projects = FXCollections.observableArrayList();
    private List<TeamMember> availableMembers = new ArrayList<>();

    @FXML
    public void initialize() {
        System.out.println("✅ CreateProjectController initialized");
        
        // Set today as default start date
        startDatePicker.setValue(LocalDate.now());
        
        // Set default end date to 30 days from now
        endDatePicker.setValue(LocalDate.now().plusDays(30));
        
        // Initialize color combo box
        colorComboBox.setItems(FXCollections.observableArrayList(
            "#6366f1", "#f59e0b", "#10b981", "#ef4444", "#3b82f6", "#8b5cf6", "#ec4899"
        ));
        colorComboBox.setValue("#6366f1");
        
        // Load available team members
        loadTeamMembers();
        
        // Disable create button initially
        createButton.setDisable(true);
        
        // Enable create button only when project name is filled
        projectNameField.textProperty().addListener((obs, old, newVal) -> {
            createButton.setDisable(newVal.trim().isEmpty());
        });
    }
    
    private void loadTeamMembers() {
        // In real app, this would load from database
        // For now, load sample team members
        availableMembers.add(new TeamMember(1, "John Doe", "Engineering"));
        availableMembers.add(new TeamMember(2, "Sarah Johnson", "Engineering"));
        availableMembers.add(new TeamMember(3, "Mike Chen", "Design"));
        availableMembers.add(new TeamMember(4, "Alice Brown", "Product"));
        availableMembers.add(new TeamMember(5, "Bob Wilson", "Engineering"));
        availableMembers.add(new TeamMember(6, "Diana Prince", "Design"));
        
        // Create checkboxes for each member
        ObservableList<CheckBox> checkBoxes = FXCollections.observableArrayList();
        for (TeamMember member : availableMembers) {
            CheckBox checkBox = new CheckBox(member.getName() + " (" + member.getDepartment() + ")");
            checkBox.setUserData(member);
            checkBoxes.add(checkBox);
        }
        
        teamMembersListView.setItems(checkBoxes);
    }
    
    @FXML
    private void handleCreate() {
        String name = projectNameField.getText().trim();
        if (name.isEmpty()) {
            showAlert("Error", "Project name is required");
            return;
        }
        
        String description = descriptionField.getText();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String color = colorComboBox.getValue();
        
        // Validate dates
        if (endDate != null && startDate != null && endDate.isBefore(startDate)) {
            showAlert("Invalid Dates", "End date cannot be before start date");
            return;
        }
        
        // Get selected team members
        List<TeamMember> selectedMembers = new ArrayList<>();
        for (CheckBox checkBox : teamMembersListView.getItems()) {
            if (checkBox.isSelected()) {
                selectedMembers.add((TeamMember) checkBox.getUserData());
            }
        }
        
        // Create new project
        Project newProject = new Project(
            generateProjectId(),
            name,
            description,
            startDate,
            endDate,
            color,
            selectedMembers
        );
        
        // Add to projects list (in real app, save to database)
        projects.add(newProject);
        
        // Show success message with details
        StringBuilder membersText = new StringBuilder();
        for (TeamMember member : selectedMembers) {
            membersText.append("\n  • ").append(member.getName()).append(" (").append(member.getDepartment()).append(")");
        }
        
        String successMessage = String.format(
            "Project '%s' created successfully!\n\n" +
            "Start Date: %s\n" +
            "End Date: %s\n" +
            "Team Members (%d):%s",
            name,
            startDate,
            endDate,
            selectedMembers.size(),
            membersText.toString()
        );
        
        showInfo("Success", successMessage);
        
        // Close window
        handleCancel();
    }
    
    private int generateProjectId() {
        return projects.size() + 1;
    }
    
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
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
    
    // Method to get created projects (for parent controller)
    public ObservableList<Project> getProjects() {
        return projects;
    }
    
    // Inner class for Team Member
    public static class TeamMember {
        private int id;
        private String name;
        private String department;
        
        public TeamMember(int id, String name, String department) {
            this.id = id;
            this.name = name;
            this.department = department;
        }
        
        public int getId() { return id; }
        public String getName() { return name; }
        public String getDepartment() { return department; }
    }
    
    // Inner class for Project
    public static class Project {
        private int id;
        private String name;
        private String description;
        private LocalDate startDate;
        private LocalDate endDate;
        private String color;
        private List<TeamMember> teamMembers;
        
        public Project(int id, String name, String description, LocalDate startDate, 
                      LocalDate endDate, String color, List<TeamMember> teamMembers) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.startDate = startDate;
            this.endDate = endDate;
            this.color = color;
            this.teamMembers = teamMembers;
        }
        
        public int getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public LocalDate getStartDate() { return startDate; }
        public LocalDate getEndDate() { return endDate; }
        public String getColor() { return color; }
        public List<TeamMember> getTeamMembers() { return teamMembers; }
        public int getMemberCount() { return teamMembers.size(); }
    }
}