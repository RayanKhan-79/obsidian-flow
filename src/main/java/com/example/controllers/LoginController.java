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
        // Load logo (comment out if no logo yet)
        try {
            Image logo = new Image(getClass().getResourceAsStream("src\\main\\resources\\com\\example\\images\\logo.jpeg"));
            logoImage.setImage(logo);
        } catch (Exception e) {
            System.out.println("Logo not found, continuing without logo");
            logoImage.setVisible(false); // Hide if no logo
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
        usernameField.setPromptText("Username (try 'demo')");
        passwordField.setPromptText("Password (try 'password123')");
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

        // Simulate network delay with a simple animation
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
                showError("Invalid username or password. Try 'demo' / 'password123'");
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
            
            // Add fade transition
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
            showError("Could not load signup page: " + e.getMessage());
        }
    }

   private void navigateToDashboard() {
    try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fxml/dashboard.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) signupButton.getScene().getWindow();
            
            // Add fade transition
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
            showError("Could not load dashboard page: " + e.getMessage());
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
        alert.setTitle("Forgot Password");
        alert.setHeaderText(null);
        alert.setContentText("For demo, use:\nUsername: demo\nPassword: password123");
        alert.showAndWait();
    }
}