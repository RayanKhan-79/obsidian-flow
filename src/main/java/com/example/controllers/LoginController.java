package com.example.controllers;

import com.example.models.User;
import com.example.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginController {
    
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private Button signupButton;
    @FXML private CheckBox rememberMeCheck;
    @FXML private ImageView logoImage;
    @FXML private AnchorPane rootPane;
    @FXML private ProgressIndicator loadingIndicator;

    @FXML
    public void initialize() {
        // Load logo
        try {
            // Fix the logo path
            Image logo = new Image(getClass().getResourceAsStream("/com/example/images/logo.jpeg"));
            if (logo != null) {
                logoImage.setImage(logo);
            } else {
                logoImage.setVisible(false);
            }
        } catch (Exception e) {
            System.out.println("Logo not found, continuing without logo: " + e.getMessage());
            logoImage.setVisible(false);
        }

        // Add enter key handler
        usernameField.setOnAction(e -> passwordField.requestFocus());
        passwordField.setOnAction(e -> handleLogin());
        
        // Style the login button
        loginButton.setStyle(
            "-fx-background-color: #4CAF50;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;"
        );

        // Add demo credentials hint
        usernameField.setPromptText("Username (try 'sarah' for manager, 'john' for member)");
        passwordField.setPromptText("Password (try 'password123')");
        
        // Hide error label initially
        errorLabel.setVisible(false);
        loadingIndicator.setVisible(false);
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        // Show loading
        loadingIndicator.setVisible(true);
        loginButton.setDisable(true);

        // Simulate network delay
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(
            javafx.util.Duration.seconds(0.5)
        );
        pause.setOnFinished(e -> {
            // Authenticate user
            User user = User.loginUser(username, password);
            
            loadingIndicator.setVisible(false);
            loginButton.setDisable(false);
            
            if (user != null) {
                // Store user in session
                SessionManager.setCurrentUser(user);
                navigateToDashboard();
            } else {
                showError("Invalid username or password. Try:\n" +
                         "Manager: sarah / password123\n" +
                         "Member: john / password123\n" +
                         "Admin: admin / admin123");
            }
        });
        pause.play();
    }

    @FXML
    private void handleSignup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fxml/signup.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) signupButton.getScene().getWindow();
            
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Task Manager - Sign Up");
            stage.setMinWidth(1000);
            stage.setMinHeight(600);
            stage.setResizable(true);
            stage.centerOnScreen();
            
            // Fade in animation
            root.setOpacity(0);
            javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(
                javafx.util.Duration.millis(300), root);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not load signup page: " + e.getMessage());
        }
    }

    private void navigateToDashboard() {
        try {
            // Get current user from SessionManager, not from local variable
            User currentUser = SessionManager.getCurrentUser();
            
            if (currentUser == null) {
                showError("User session not found");
                return;
            }
            
            System.out.println("📂 Loading dashboard for: " + currentUser.getFullName() + 
                             " (" + currentUser.getRole() + ")");
            
            String fxmlFile;
            String title;
            
            // Choose dashboard based on user role
            switch(currentUser.getRole()) {
                case "Admin":
                case "Project Manager":
                    fxmlFile = "/com/example/fxml/dashboard.fxml"; // Full dashboard
                    title = "Task Manager - Dashboard";
                    break;
                case "Member":
                    fxmlFile = "/com/example/fxml/MemberDashboard.fxml"; // Member dashboard
                    title = "My Tasks - Member Dashboard";
                    break;
                case "Viewer":
                    fxmlFile = "/com/example/fxml/ViewerDashboard.fxml"; // Read-only dashboard
                    title = "Task Manager - Viewer";
                    break;
                default:
                    fxmlFile = "/com/example/fxml/MemberDashboard.fxml";
                    title = "Dashboard";
            }
            
            System.out.println("  Looking for: " + fxmlFile);
            
            // Try multiple paths
            String[] possiblePaths = {
                fxmlFile,
                fxmlFile.replace("/com/example/fxml/", "/fxml/"),
                "/fxml/" + fxmlFile.substring(fxmlFile.lastIndexOf("/") + 1)
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
                System.err.println("❌ Could not find dashboard file!");
                showError("Could not load dashboard");
                return;
            }
            
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.centerOnScreen();
            stage.show();
            
            System.out.println("✅ Dashboard loaded successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
            showError("Error loading dashboard: " + e.getMessage());
        }
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-size: 12px;");
        
        // Auto-hide after 3 seconds
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(
            javafx.util.Duration.seconds(3)
        );
        pause.setOnFinished(e -> errorLabel.setVisible(false));
        pause.play();
    }

    @FXML
    private void handleForgotPassword() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Demo Credentials");
        alert.setHeaderText(null);
        alert.setContentText(
            "Demo Users:\n\n" +
            "Admin: admin / admin123\n" +
            "Project Manager: sarah / password123\n" +
            "Member: john / password123\n" +
            "Member: mike / password123\n" +
            "Viewer: bob / password123"
        );
        alert.showAndWait();
    }
}