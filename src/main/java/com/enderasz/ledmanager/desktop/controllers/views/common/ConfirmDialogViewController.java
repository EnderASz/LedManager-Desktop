package com.enderasz.ledmanager.desktop.controllers.views.common;

import com.enderasz.ledmanager.desktop.views.LoadedView;
import com.enderasz.ledmanager.desktop.views.View;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;


public class ConfirmDialogViewController {
    @FXML
    private GridPane rootNode;
    @FXML
    private Label promptLabel;
    @FXML
    private Button submitBtn;
    @FXML
    private Button cancelBtn;

    private Runnable onSubmit;

    @FXML
    private void initialize() {
        Platform.runLater(cancelBtn::requestFocus);
    }

    @FXML
    private void handleSubmitAction(ActionEvent actionEvent) {
        onSubmit.run();
        close();
    }

    @FXML
    private void handleCancelAction(ActionEvent actionEvent) {
        close();
    }

    private void close() {
        ((Stage) rootNode.getScene().getWindow()).close();
    }

    static public ConfirmDialogViewController createAndShow(
            String title,
            String promptText,
            String confirmBtnText,
            String cancelBtnText,
            Runnable onSubmit
    ) {
        return createAndShow(title, promptText, confirmBtnText, cancelBtnText, onSubmit, null);
    }

    static public ConfirmDialogViewController createAndShow(
            String title,
            String promptText,
            String confirmBtnText,
            String cancelBtnText,
            Runnable onSubmit,
            Window owner
    ) {
        LoadedView view = View.GENERIC_CONFIRM_DIALOG.load();
        Scene scene = view.newScene();
        ConfirmDialogViewController controller = view.getLoader().getController();

        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(scene);

        controller.promptLabel.setText(promptText);
        controller.submitBtn.setText(confirmBtnText);
        controller.cancelBtn.setText(cancelBtnText);
        controller.onSubmit = onSubmit;

        if (owner != null) {
            stage.initOwner(owner);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.showAndWait();
        } else {
            stage.show();
        }

        return controller;
    }

}
