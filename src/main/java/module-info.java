module com.example.lab4 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.lab5 to javafx.fxml;
    exports com.example.lab5;
//    opens com.example.lab5.rasterizer to javafx.fxml;
}