package com.example.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class AddMemberDialogController {

    @FXML private Label projectTitleLabel;
    @FXML private TextField searchUserField;
    @FXML private Button searchButton;
    @FXML private ListView<String> availableUsersList;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Button addMemberButton;
    @FXML private TableView<ProjectMember> currentMembersTable;
    @FXML private TableColumn<ProjectMember, String> memberNameColumn;
    @FXML private TableColumn<ProjectMember, String> memberEmailColumn;
    @FXML private TableColumn<ProjectMember, String> memberRoleColumn;
    @FXML private TableColumn<ProjectMember, String> memberActionColumn;
    @FXML private Button closeButton;
    @FXML private Label totalMembersLabel;
    
    private String projectName;
    private ObservableList<ProjectMember> members = FXCollections.observableArrayList();
    private ObservableList<String> availableUsers = FXCollections.observableArrayList();
    
    // Flags for tracking if member was added
    private boolean memberAdded = false;
    private TeamMember newlyAddedMember = null;

    @FXML
    public void initialize() {
        System.out.println("✅ AddMemberDialogController initialized");
        
        setupTableColumns();
        setupComboBoxes();
        loadSampleData();
        setupEventHandlers();
        updateTotalMembers();
        
        // Force initial refresh
        availableUsersList.refresh();
        currentMembersTable.refresh();
    }
    
    private void setupTableColumns() {
        // Setup member table columns
        memberNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        memberEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        memberRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        
        // Setup action column with remove buttons
        memberActionColumn.setCellFactory(col -> new TableCell<ProjectMember, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Button removeBtn = new Button("❌");
                    removeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-cursor: hand; -fx-font-size: 14px;");
                    removeBtn.setOnAction(e -> {
                        ProjectMember member = getTableView().getItems().get(getIndex());
                        handleRemoveMember(member);
                    });
                    
                    Tooltip.install(removeBtn, new Tooltip("Remove member"));
                    setGraphic(removeBtn);
                }
            }
        });
        
        currentMembersTable.setItems(members);
    }
    
    private void setupComboBoxes() {
        roleComboBox.setItems(FXCollections.observableArrayList(
            "Admin", "Member", "Viewer", "Manager", "Developer"
        ));
        roleComboBox.setValue("Member");
    }
    
    private void setupEventHandlers() {
        searchButton.setOnAction(e -> handleSearch());
        addMemberButton.setOnAction(e -> handleAddMember());
        closeButton.setOnAction(e -> handleClose());
        
        // Add enter key handler for search
        searchUserField.setOnAction(e -> handleSearch());
        
        // Add selection listener to available users list
        availableUsersList.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            addMemberButton.setDisable(newVal == null);
            if (newVal != null) {
                System.out.println("Selected: " + newVal);
            }
        });
        
        // Double-click to add member
        availableUsersList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String selected = availableUsersList.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    handleAddMember();
                }
            }
        });
        
        // Initial disable state
        addMemberButton.setDisable(true);
    }
    
    public void setProjectName(String name) {
        this.projectName = name;
        if (projectTitleLabel != null) {
            projectTitleLabel.setText("Manage Members - " + name);
        }
    }
    
    private void loadSampleData() {
        // Clear existing data
        availableUsers.clear();
        members.clear();
        
        // Load available users with more variety
        availableUsers.addAll(
            "Alice Johnson (alice.johnson@email.com) - Engineering",
            "Bob Smith (bob.smith@email.com) - Design",
            "Charlie Brown (charlie.brown@email.com) - Product",
            "Diana Prince (diana.prince@email.com) - Marketing",
            "Eve Adams (eve.adams@email.com) - Engineering",
            "Frank Miller (frank.miller@email.com) - Sales",
            "Grace Hopper (grace.hopper@email.com) - Engineering",
            "Henry Ford (henry.ford@email.com) - Operations",
            "Ivy Chen (ivy.chen@email.com) - Design",
            "Jack Wilson (jack.wilson@email.com) - Product"
        );
        availableUsersList.setItems(availableUsers);
        
        // Set cell factory to ensure proper display
        availableUsersList.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-padding: 8; -fx-font-size: 13px;");
                }
            }
        });
        
        // Load current members (sample)
        members.add(new ProjectMember("John Doe", "john.doe@email.com", "Admin"));
        members.add(new ProjectMember("Sarah Johnson", "sarah.johnson@email.com", "Member"));
        members.add(new ProjectMember("Mike Chen", "mike.chen@email.com", "Member"));
        
        currentMembersTable.setItems(members);
        
        System.out.println("📊 Loaded " + availableUsers.size() + " available users and " + 
                          members.size() + " current members");
        updateTotalMembers();
    }
    
    @FXML
    private void handleSearch() {
        String query = searchUserField.getText().toLowerCase().trim();
        System.out.println("🔍 Searching for: '" + query + "'");
        
        if (query.isEmpty()) {
            availableUsersList.setItems(availableUsers);
            System.out.println("  Showing all " + availableUsers.size() + " users");
        } else {
            ObservableList<String> filtered = FXCollections.observableArrayList();
            for (String user : availableUsers) {
                if (user.toLowerCase().contains(query)) {
                    filtered.add(user);
                }
            }
            availableUsersList.setItems(filtered);
            System.out.println("  Found " + filtered.size() + " matching users");
        }
        
        // Force refresh
        availableUsersList.refresh();
    }
    
    @FXML
    private void handleAddMember() {
        String selectedUser = availableUsersList.getSelectionModel().getSelectedItem();
        String selectedRole = roleComboBox.getValue();
        
        System.out.println("➕ Adding member: " + selectedUser + " as " + selectedRole);
        
        if (selectedUser == null) {
            showAlert("Error", "Please select a user to add");
            return;
        }
        
        if (selectedRole == null) {
            showAlert("Error", "Please select a role");
            return;
        }
        
        try {
            // Parse user info - improved parsing
            String[] parts = selectedUser.split(" \\(");
            String name = parts[0];
            
            // Parse email and department
            String remaining = parts[1];
            String email = remaining.split("\\)")[0];
            
            String department = "General";
            if (remaining.contains("-")) {
                String[] deptParts = remaining.split(" - ");
                if (deptParts.length > 1) {
                    department = deptParts[1].trim();
                }
            }
            
            // Check if user already exists in members
            boolean alreadyExists = false;
            for (ProjectMember member : members) {
                if (member.getEmail().equals(email)) {
                    alreadyExists = true;
                    break;
                }
            }
            
            if (alreadyExists) {
                showAlert("Error", "User is already a member of this project");
                return;
            }
            
            // Add to members table
            ProjectMember newProjectMember = new ProjectMember(name, email, selectedRole);
            members.add(newProjectMember);
            currentMembersTable.setItems(members);
            
            // Create TeamMember for return value
            newlyAddedMember = new TeamMember(name, email, selectedRole, department, "Active");
            memberAdded = true;
            
            // Remove from available list
            availableUsers.remove(selectedUser);
            availableUsersList.setItems(availableUsers);
            
            // Clear selection
            availableUsersList.getSelectionModel().clearSelection();
            searchUserField.clear();
            
            // Refresh both views
            availableUsersList.refresh();
            currentMembersTable.refresh();
            
            updateTotalMembers();
            
            // Show success
            showInfo("Success", name + " added as " + selectedRole);
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not add member: " + e.getMessage());
        }
    }
    
    private void handleRemoveMember(ProjectMember member) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Removal");
        confirm.setHeaderText(null);
        confirm.setContentText("Remove " + member.getName() + " from project?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                members.remove(member);
                
                // Add back to available users
                String userEntry = member.getName() + " (" + member.getEmail() + ") - Previously Added";
                availableUsers.add(userEntry);
                availableUsersList.setItems(availableUsers);
                
                // Refresh
                availableUsersList.refresh();
                currentMembersTable.refresh();
                
                updateTotalMembers();
                showInfo("Success", member.getName() + " removed from project");
            }
        });
    }
    
    private void updateTotalMembers() {
        if (totalMembersLabel != null) {
            totalMembersLabel.setText("Total Members: " + members.size());
        }
    }
    
    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
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
    
    /**
     * Check if a member was added in this dialog session
     * @return true if a member was added
     */
    public boolean isAdded() {
        return memberAdded;
    }
    
    /**
     * Get the newly added team member
     * @return the newly added TeamMember object
     */
    public TeamMember getNewMember() {
        return newlyAddedMember;
    }
    
    // Inner class for Project Member (for the table)
    public static class ProjectMember {
        private final SimpleStringProperty name;
        private final SimpleStringProperty email;
        private final SimpleStringProperty role;
        
        public ProjectMember(String name, String email, String role) {
            this.name = new SimpleStringProperty(name);
            this.email = new SimpleStringProperty(email);
            this.role = new SimpleStringProperty(role);
        }
        
        public String getName() { return name.get(); }
        public void setName(String name) { this.name.set(name); }
        
        public String getEmail() { return email.get(); }
        public void setEmail(String email) { this.email.set(email); }
        
        public String getRole() { return role.get(); }
        public void setRole(String role) { this.role.set(role); }
    }
    
    // Inner class for Team Member (return type)
    public static class TeamMember {
        private final SimpleStringProperty name;
        private final SimpleStringProperty email;
        private final SimpleStringProperty role;
        private final SimpleStringProperty department;
        private final SimpleStringProperty status;
        
        public TeamMember(String name, String email, String role, String department, String status) {
            this.name = new SimpleStringProperty(name);
            this.email = new SimpleStringProperty(email);
            this.role = new SimpleStringProperty(role);
            this.department = new SimpleStringProperty(department);
            this.status = new SimpleStringProperty(status);
        }
        
        public String getName() { return name.get(); }
        public String getEmail() { return email.get(); }
        public String getRole() { return role.get(); }
        public String getDepartment() { return department.get(); }
        public String getStatus() { return status.get(); }
    }
}