module importing.app {
    requires javafx.controls;
    requires javafx.fxml;

    opens importing.app to javafx.fxml;
    exports importing.app;
}
