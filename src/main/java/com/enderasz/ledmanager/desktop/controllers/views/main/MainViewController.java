package com.enderasz.ledmanager.desktop.controllers.views.main;

import com.enderasz.ledmanager.desktop.connection.ServerConnection;
import com.enderasz.ledmanager.desktop.controllers.views.main.project.ProjectViewController;
import com.enderasz.ledmanager.desktop.views.LoadedView;
import com.enderasz.ledmanager.desktop.views.View;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainViewController {
    @FXML
    public TabPane rootNode;

    @FXML
    public AnchorPane projectViewAnchor;
    @FXML
    private ProjectViewController projectViewController;

    @FXML
    private void initialize() {
        initializeProjectView();
    }

    private void initializeProjectView() {
        // View loading and initialization is not necessary here as it's done by fx:include
        projectViewController.registerOnProjectNameChange(this::updateWindowTitle);
        projectViewController.registerOnNewProjectInitialization(this::reinitializeProjectView);
        projectViewController.setServerConnectionSupplier(()->this.connection);
    }

    ServerConnection connection;

    public void setConnection(ServerConnection connection) {
        this.connection = connection;
    }

    private void reinitializeProjectView() {
        LoadedView newProjectTab = View.MAIN_PROJECT_TAB_SUBVIEW.load();

        projectViewAnchor.getChildren().clear();
        projectViewAnchor.getChildren().add(newProjectTab.getNode());
        projectViewController = newProjectTab.getLoader().getController();
        initializeProjectView();
        updateWindowTitle();
    }

    public void updateWindowTitle() {
        ((Stage) rootNode.getScene().getWindow()).setTitle(
            "LedManager - %s - %s".formatted(
                connection.getServerName(),
                projectViewController.getProjectName()
            )
        );
    }
}
