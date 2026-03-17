package com.example.controllers;

import com.example.models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class SignupController {
    
    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;
    @FXML private Label passwordStrengthLabel;
    @FXML private Button signupButton;
    @FXML private Button backToLoginButton;
    @FXML private ImageView logoImage;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private CheckBox termsCheck;

    // Password strength indicators
    @FXML private Label lengthCheck;
    @FXML private Label uppercaseCheck;
    @FXML private Label lowercaseCheck;
    @FXML private Label numberCheck;

    @FXML
    public void initialize() {
        // Load logo (comment out if no logo yet)
        try {
            Image logo = new Image(getClass().getResourceAsStream("D:\\Downloads\\VS Code\\Github\\obsidian-flow\\src\\main\\resources\\com\\example\\images\\logo.jpeg"));
            logoImage.setImage(logo);
        } catch (Exception e) {
            System.out.println("Logo not found, continuing without logo");
            logoImage.setVisible(false);
        }

        // Style signup button
        signupButton.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;"
        );

        // Initialize password check labels
        lengthCheck.setText("✗ At least 8 characters");
        uppercaseCheck.setText("✗ At least one uppercase letter");
        lowercaseCheck.setText("✗ At least one lowercase letter");
        numberCheck.setText("✗ At least one number");

        // Add real-time validation
        passwordField.textProperty().addListener((obs, old, newVal) -> {
            validatePasswordStrength(newVal);
        });
    }

    private void validatePasswordStrength(String password) {
        if (password.isEmpty()) {
            resetPasswordChecks();
            return;
        }

        boolean hasLength = password.length() >= 8;
        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasLowercase = password.matches(".*[a-z].*");
        boolean hasNumber = password.matches(".*\\d.*");

        // Update indicators
        updateCheckLabel(lengthCheck, hasLength, "At least 8 characters");
        updateCheckLabel(uppercaseCheck, hasUppercase, "At least one uppercase letter");
        updateCheckLabel(lowercaseCheck, hasLowercase, "At least one lowercase letter");
        updateCheckLabel(numberCheck, hasNumber, "At least one number");

        // Calculate strength
        int strength = 0;
        if (hasLength) strength++;
        if (hasUppercase) strength++;
        if (hasLowercase) strength++;
        if (hasNumber) strength++;

        // Update strength label
        switch (strength) {
            case 0:
            case 1:
            case 2:
                passwordStrengthLabel.setText("Weak");
                passwordStrengthLabel.setStyle("-fx-text-fill: #d32f2f;");
                break;
            case 3:
                passwordStrengthLabel.setText("Medium");
                passwordStrengthLabel.setStyle("-fx-text-fill: #f57c00;");
                break;
            case 4:
                passwordStrengthLabel.setText("Strong");
                passwordStrengthLabel.setStyle("-fx-text-fill: #388e3c;");
                break;
        }
    }

    private void resetPasswordChecks() {
        updateCheckLabel(lengthCheck, false, "At least 8 characters");
        updateCheckLabel(uppercaseCheck, false, "At least one uppercase letter");
        updateCheckLabel(lowercaseCheck, false, "At least one lowercase letter");
        updateCheckLabel(numberCheck, false, "At least one number");
        passwordStrengthLabel.setText("");
    }

    private void updateCheckLabel(Label label, boolean valid, String text) {
        if (valid) {
            label.setText("✓ " + text);
            label.setStyle("-fx-text-fill: #388e3c;");
        } else {
            label.setText("✗ " + text);
            label.setStyle("-fx-text-fill: #d32f2f;");
        }
    }

    @FXML
    private void handleSignup() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validate inputs
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || 
            password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }

        // Simple email validation
        if (!email.contains("@") || !email.contains(".")) {
            showError("Please enter a valid email address");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        // Password strength validation
        if (password.length() < 8 || !password.matches(".*[A-Z].*") || 
            !password.matches(".*[a-z].*") || !password.matches(".*\\d.*")) {
            showError("Password must meet all requirements");
            return;
        }

        if (!termsCheck.isSelected()) {
            showError("Please accept the terms and conditions");
            return;
        }

        // Show loading
        loadingIndicator.setVisible(true);
        signupButton.setDisable(true);

        // Simulate processing
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(
            javafx.util.Duration.seconds(0.5)
        );
        pause.setOnFinished(e -> {
            // Create new user
            User newUser = new User(username, email, password, fullName, confirmPassword, confirmPassword);
            boolean registered = User.registerUser(newUser);
            
            loadingIndicator.setVisible(false);
            signupButton.setDisable(false);
            
            if (registered) {
                showSuccessAndNavigate();
            } else {
                showError("Username or email already exists");
            }
        });
        pause.play();
    }

    private void showSuccessAndNavigate() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Account created successfully! You can now log in.");
        alert.showAndWait();
        
        // Navigate back to login
        handleBackToLogin();
    }

    @FXML
    private void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fxml/login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) backToLoginButton.getScene().getWindow();
            
            // Add fade transition
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Task Manager - Login");
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
            showError("Could not load login page: " + e.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(
            javafx.util.Duration.seconds(3)
        );
        pause.setOnFinished(e -> errorLabel.setVisible(false));
        pause.play();
    }
}