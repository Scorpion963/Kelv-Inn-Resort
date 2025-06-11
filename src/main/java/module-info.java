module com.example.finalproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;

    opens com.example.finalproject to javafx.fxml, com.fasterxml.jackson.databind;

    exports com.example.finalproject;
}
