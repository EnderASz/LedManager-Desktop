module com.enderasz.ledmanager.desktop {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.naming;
    requires java.desktop;


    opens com.enderasz.ledmanager.desktop to javafx.fxml;
    exports com.enderasz.ledmanager.desktop;

    exports com.enderasz.ledmanager.desktop.views;

    opens com.enderasz.ledmanager.desktop.controllers.views.common to javafx.fxml;
    exports com.enderasz.ledmanager.desktop.controllers.views.common;

    opens com.enderasz.ledmanager.desktop.controllers.views.main to javafx.fxml;
    exports com.enderasz.ledmanager.desktop.controllers.views.main;

    opens com.enderasz.ledmanager.desktop.controllers.views.main.project to javafx.fxml;
    exports com.enderasz.ledmanager.desktop.controllers.views.main.project;

    opens com.enderasz.ledmanager.desktop.controllers.views.main.project.modes to javafx.fxml;
    exports com.enderasz.ledmanager.desktop.controllers.views.main.project.modes;

    exports com.enderasz.ledmanager.desktop.data.config;
    exports com.enderasz.ledmanager.desktop.controllers.views.main.project.lightlist;
    opens com.enderasz.ledmanager.desktop.controllers.views.main.project.lightlist to javafx.fxml;
    exports com.enderasz.ledmanager.desktop.controllers.views.connect;
    opens com.enderasz.ledmanager.desktop.controllers.views.connect to javafx.fxml;
}