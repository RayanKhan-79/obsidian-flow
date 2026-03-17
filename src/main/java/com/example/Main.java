package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("Loading login screen...");
            
            // Use the correct path based on your resources folder
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/Login.fxml")  // Try this path first
            );
            
            // Alternative path if above doesn't work
            if (loader.getLocation() == null) {
                System.out.println("Trying alternative path...");
                loader = new FXMLLoader(
                    getClass().getResource("/com/example/fxml/Login.fxml")
                );
            }
            
            if (loader.getLocation() == null) {
                System.err.println("ERROR: Could not find login.fxml");
                System.err.println("Current working directory: " + System.getProperty("user.dir"));
                System.err.println("Classpath: " + System.getProperty("java.class.path"));
                return;
            }
            
            Parent root = loader.load();
            System.out.println("Login screen loaded successfully");
            
            Scene scene = new Scene(root);
            
            // Set application icon
            try {
                Image icon = new Image(getClass().getResourceAsStream("/images/logo.png"));
                if (icon != null) {
                    primaryStage.getIcons().add(icon);
                }
            } catch (Exception e) {
                System.out.println("No icon found: " + e.getMessage());
            }
            
            primaryStage.setTitle("Task Manager - Login");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1300);
            primaryStage.setMinHeight(600);
            primaryStage.setResizable(true);
            primaryStage.centerOnScreen();
            primaryStage.show();
            
            System.out.println("Application started successfully");
            
        } catch (Exception e) {
            System.err.println("ERROR starting application: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}