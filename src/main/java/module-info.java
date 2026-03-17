module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    
    opens com.example to javafx.fxml;
    opens com.example.controllers to javafx.fxml;
    opens com.example.models to javafx.base;  
    opens com.example.utils to javafx.fxml;
    
    exports com.example;
    exports com.example.controllers;
    exports com.example.models;
    exports com.example.utils;
}