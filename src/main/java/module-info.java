module com.example 
{
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    
    opens com.example to javafx.fxml;
    opens com.example.backend.models to javafx.fxml;
        
    exports com.example;
    exports com.example.backend.models;
}