module com.example 
{
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example to javafx.fxml;
    opens com.example.backend.model to javafx.fxml;
        
    exports com.example;
    exports com.example.backend.model;
}
