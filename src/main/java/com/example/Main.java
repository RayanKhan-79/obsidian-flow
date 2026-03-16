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
            
            // Load login screen
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/fxml/login.fxml")
            );
            
            if (loader.getLocation() == null) {
                System.err.println("ERROR: Could not find login.fxml");
                return;
            }
            
            Parent root = loader.load();
            System.out.println("Login screen loaded successfully");
            
            // Set up scene
            Scene scene = new Scene(root);
            
            // Set application icon
            try {
                Image icon = new Image(getClass().getResourceAsStream("/com/example/images/logo.png"));
                if (icon != null) {
                    primaryStage.getIcons().add(icon);
                    System.out.println("Icon loaded successfully");
                }
            } catch (Exception e) {
                System.out.println("No icon found: " + e.getMessage());
            }
            
            // Configure stage
            primaryStage.setTitle("Task Manager - Login");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1000);
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
        System.out.println("Starting Task Manager...");
        System.out.println("Java version: " + System.getProperty("java.version"));
        launch(args);
    }
}