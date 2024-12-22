module com.enderasz.ledmanager.desktop {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.enderasz.ledmanager.desktop to javafx.fxml;
    exports com.enderasz.ledmanager.desktop;
    exports com.enderasz.ledmanager.desktop.controllers.views;
}