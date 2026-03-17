package com.example.frontend.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Modality;
import java.io.IOException;

public class TeamController {

    @FXML private Button dashboardBtn;
    @FXML private Button projectsBtn;
    @FXML private Button tasksBtn;
    @FXML private Button teamBtn;
    @FXML private Button reportsBtn;
    @FXML private Button settingsBtn;
    @FXML private Button logoutBtn;
    
    @FXML private TextField searchField;
    @FXML private Button addMemberButton;
    @FXML private TableView<TeamMember> teamTable;
    @FXML private TableColumn<TeamMember, String> nameColumn;
    @FXML private TableColumn<TeamMember, String> emailColumn;
    @FXML private TableColumn<TeamMember, String> roleColumn;
    @FXML private TableColumn<TeamMember, String> departmentColumn;
    @FXML private TableColumn<TeamMember, String> statusColumn;
    @FXML private TableColumn<TeamMember, String> actionsColumn;
    
    @FXML private Label totalMembersLabel;
    @FXML private Label activeMembersLabel;
    @FXML private Label adminCountLabel;

    private ObservableList<TeamMember> teamMembers = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        System.out.println("✅ TeamController initialized");
        
        setupTableColumns();
        loadSampleData();
        setupNavigation();
        setupSearch();
        updateStats();
    }
    
    private void setupTableColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Setup actions column with both Edit and Delete buttons
        actionsColumn.setCellFactory(col -> new TableCell<TeamMember, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5);
                    
                    Button editBtn = new Button("✏️");
                    editBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 14px;");
                    editBtn.setOnAction(e -> {
                        TeamMember member = getTableView().getItems().get(getIndex());
                        handleEditMember(member);
                    });
                    
                    Button deleteBtn = new Button("🗑️");
                    deleteBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 14px; -fx-text-fill: #ef4444;");
                    deleteBtn.setOnAction(e -> {
                        TeamMember member = getTableView().getItems().get(getIndex());
                        handleDeleteMember(member);
                    });
                    
                    buttons.getChildren().addAll(editBtn, deleteBtn);
                    setGraphic(buttons);
                }
            }
        });
        
        teamTable.setItems(teamMembers);
    }
    
    private void loadSampleData() {
        teamMembers.addAll(
            new TeamMember("John Doe", "john@email.com", "Admin", "Engineering", "Active"),
            new TeamMember("Sarah Johnson", "sarah@email.com", "Member", "Engineering", "Active"),
            new TeamMember("Mike Chen", "mike@email.com", "Member", "Design", "Active"),
            new TeamMember("Alice Brown", "alice@email.com", "Admin", "Product", "Active"),
            new TeamMember("Bob Wilson", "bob@email.com", "Member", "Engineering", "Inactive"),
            new TeamMember("Diana Prince", "diana@email.com", "Member", "Design", "Active"),
            new TeamMember("Charlie Smith", "charlie@email.com", "Viewer", "Marketing", "Active"),
            new TeamMember("Eva Green", "eva@email.com", "Member", "Engineering", "Inactive")
        );
    }
    
    private void setupNavigation() {
        dashboardBtn.setOnAction(e -> loadPage("dashboard"));
        projectsBtn.setOnAction(e -> loadPage("projects"));
        tasksBtn.setOnAction(e -> loadPage("tasks"));
        teamBtn.setOnAction(e -> loadPage("team"));
        reportsBtn.setOnAction(e -> loadPage("reports"));
        settingsBtn.setOnAction(e -> loadPage("settings"));
        logoutBtn.setOnAction(e -> handleLogout());
        
        addMemberButton.setOnAction(e -> handleAddMember());
    }
    
    private void setupSearch() {
        searchField.textProperty().addListener((obs, old, newVal) -> {
            filterMembers(newVal);
        });
    }
    
    private void loadPage(String page) {
        try {
            String fxmlFile = "/com/example/fxml/" + page.substring(0,1).toUpperCase() + page.substring(1) + ".fxml";
            System.out.println("Loading: " + fxmlFile);
            
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) dashboardBtn.getScene().getWindow();
            stage.setScene(new Scene(root,1200,800));
            stage.setTitle("Task Manager - " + page.substring(0,1).toUpperCase() + page.substring(1));
            stage.centerOnScreen();
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load " + page + " page");
        }
    }
    
    private void handleLogout() {
        try {
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
    
@FXML
private void handleAddMember() {
    try {
        System.out.println("📂 Opening add member dialog...");
        
        // Try multiple paths for AddMemberDialog.fxml
        String[] possiblePaths = {
            "/com/example/fxml/AddMemberDialog.fxml",
            "/fxml/AddMemberDialog.fxml",
            "/AddMemberDialog.fxml",
            "/com/example/fxml/AddMember.fxml"
        };
        
        java.net.URL fxmlUrl = null;
        for (String path : possiblePaths) {
            fxmlUrl = getClass().getResource(path);
            if (fxmlUrl != null) {
                System.out.println("✅ Found at: " + path);
                break;
            }
        }
        
        if (fxmlUrl == null) {
            showAlert("Error", "Could not find Add Member dialog");
            return;
        }
        
        // Load the Add Member Dialog
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();
        
        // Get the controller
        AddMemberDialogController dialogController = loader.getController();
        
        // Set project name if needed
        dialogController.setProjectName("Team Management");
        
        // Create new stage
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Add Team Member");
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setScene(new Scene(root));
        dialogStage.setResizable(false);
        dialogStage.showAndWait();
        
        // Check if member was added
        if (dialogController.isAdded()) {
            AddMemberDialogController.TeamMember newMember = dialogController.getNewMember();
            
            if (newMember != null) {
                // Convert to TeamController.TeamMember and add to list
                TeamMember member = new TeamMember(
                    newMember.getName(),
                    newMember.getEmail(),
                    newMember.getRole(),
                    newMember.getDepartment(),
                    newMember.getStatus()
                );
                
                teamMembers.add(member);
                teamTable.setItems(teamMembers);
                updateStats();
                
                // Show success notification
                showInfo("Success", "Team member added successfully!");
            }
        }
        
    } catch (IOException e) {
        e.printStackTrace();
        showAlert("Error", "Could not open add member dialog: " + e.getMessage());
    } catch (Exception e) {
        e.printStackTrace();
        showAlert("Error", "Unexpected error: " + e.getMessage());
    }
}
    private void handleEditMember(TeamMember member) {
        // For now, show edit dialog (can be enhanced later)
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Member");
        dialog.setHeaderText("Edit " + member.getName());
        
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Create form fields
        TextField nameField = new TextField(member.getName());
        nameField.setPromptText("Name");
        
        TextField emailField = new TextField(member.getEmail());
        emailField.setPromptText("Email");
        
        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Admin", "Member", "Viewer", "Manager", "Developer");
        roleBox.setValue(member.getRole());
        
        ComboBox<String> deptBox = new ComboBox<>();
        deptBox.getItems().addAll("Engineering", "Design", "Product", "Marketing", "Sales", "HR", "Finance");
        deptBox.setValue(member.getDepartment());
        
        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Active", "Inactive", "Pending");
        statusBox.setValue(member.getStatus());
        
        VBox content = new VBox(10);
        content.getChildren().addAll(
            new Label("Name:"), nameField,
            new Label("Email:"), emailField,
            new Label("Role:"), roleBox,
            new Label("Department:"), deptBox,
            new Label("Status:"), statusBox
        );
        
        dialogPane.setContent(content);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                member.name.set(nameField.getText());
                member.email.set(emailField.getText());
                member.role.set(roleBox.getValue());
                member.department.set(deptBox.getValue());
                member.status.set(statusBox.getValue());
                
                teamTable.refresh();
                updateStats();
                showInfo("Success", "Member updated successfully!");
            }
        });
    }
    
    private void handleDeleteMember(TeamMember member) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to remove " + member.getName() + " from the team?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                teamMembers.remove(member);
                updateStats();
                showInfo("Success", "Member removed successfully!");
            }
        });
    }
    
    private void filterMembers(String query) {
        if (query == null || query.isEmpty()) {
            teamTable.setItems(teamMembers);
        } else {
            String lowerQuery = query.toLowerCase();
            ObservableList<TeamMember> filtered = FXCollections.observableArrayList();
            for (TeamMember member : teamMembers) {
                if (member.getName().toLowerCase().contains(lowerQuery) ||
                    member.getEmail().toLowerCase().contains(lowerQuery) ||
                    member.getRole().toLowerCase().contains(lowerQuery) ||
                    member.getDepartment().toLowerCase().contains(lowerQuery)) {
                    filtered.add(member);
                }
            }
            teamTable.setItems(filtered);
        }
        updateStats();
    }
    
    private void updateStats() {
        int total = teamTable.getItems().size();
        int active = 0;
        int admins = 0;
        
        for (TeamMember member : teamTable.getItems()) {
            if ("Active".equals(member.getStatus())) {
                active++;
            }
            if ("Admin".equals(member.getRole())) {
                admins++;
            }
        }
        
        totalMembersLabel.setText(String.valueOf(total));
        activeMembersLabel.setText(String.valueOf(active));
        adminCountLabel.setText(String.valueOf(admins));
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
    
    // Inner class for Team Member
    public static class TeamMember {
        private final javafx.beans.property.SimpleStringProperty name;
        private final javafx.beans.property.SimpleStringProperty email;
        private final javafx.beans.property.SimpleStringProperty role;
        private final javafx.beans.property.SimpleStringProperty department;
        private final javafx.beans.property.SimpleStringProperty status;
        
        public TeamMember(String name, String email, String role, String department, String status) {
            this.name = new javafx.beans.property.SimpleStringProperty(name);
            this.email = new javafx.beans.property.SimpleStringProperty(email);
            this.role = new javafx.beans.property.SimpleStringProperty(role);
            this.department = new javafx.beans.property.SimpleStringProperty(department);
            this.status = new javafx.beans.property.SimpleStringProperty(status);
        }
        
        public String getName() { return name.get(); }
        public void setName(String name) { this.name.set(name); }
        
        public String getEmail() { return email.get(); }
        public void setEmail(String email) { this.email.set(email); }
        
        public String getRole() { return role.get(); }
        public void setRole(String role) { this.role.set(role); }
        
        public String getDepartment() { return department.get(); }
        public void setDepartment(String department) { this.department.set(department); }
        
        public String getStatus() { return status.get(); }
        public void setStatus(String status) { this.status.set(status); }
    }
}