module com.example 
{
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    
    opens com.example to javafx.fxml;
    opens com.example.backend.models to javafx.fxml;
    
    opens com.example.frontend.controllers to javafx.fxml;
    opens com.example.frontend.models to javafx.base;  
    opens com.example.frontend.utils to javafx.fxml;

    exports com.example;
    exports com.example.backend.models;

    exports com.example.frontend.controllers;
    exports com.example.frontend.models;
    exports com.example.frontend.utils;
}