package com.enderasz.ledmanager.desktop.controllers.views.connect;

import com.enderasz.ledmanager.desktop.connection.ConnectingStatus;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;


import java.util.ResourceBundle;

public class ConnectingDialogController {
    @FXML
    private Label statusLabel;

    @FXML
    private ResourceBundle resources;

    public void updateStatus(ConnectingStatus status) {
        statusLabel.setText(getTextForStatus(status));
        switch (status) {
            case SUCCEEDED:
            case FAILED:
            case CANCELLED:
                Scene scene = statusLabel.getScene();
                if (scene != null) {
                    // I schedule it with Platform.runLater because closing stage too early causes throwing exception on showAndWait call
                    Platform.runLater(((Stage) scene.getWindow())::close);
                }
                break;
        }
    }

    private String getTextForStatus(ConnectingStatus status) {
        return switch (status) {
            case NOT_INITIALIZED -> resources.getString("conn_dialog_status_not_initialized");
            case INITIALIZATION -> resources.getString("conn_dialog_status_initialization");
            case CONNECTING -> resources.getString("conn_dialog_status_connecting");
            case HANDSHAKING -> resources.getString("conn_dialog_status_handshaking");
            case COMPATIBILITY_CHECK -> resources.getString("conn_dialog_status_compatibility_check");
            case PRECONFIGURATION -> resources.getString("conn_dialog_status_preconfiguration");
            case SUCCEEDED -> resources.getString("conn_dialog_status_succeeded");
            case FAILED -> resources.getString("conn_dialog_status_failed");
            case CANCELLED -> resources.getString("conn_dialog_status_cancelled");
        };
    }

    public void handleCancelAction(ActionEvent actionEvent) {
        ((Stage) statusLabel.getScene().getWindow()).close();
    }
}
