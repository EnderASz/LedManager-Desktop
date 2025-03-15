package com.enderasz.ledmanager.desktop.controllers.views.common;

import com.enderasz.ledmanager.desktop.views.LoadedView;
import com.enderasz.ledmanager.desktop.views.View;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.function.Consumer;

public class SingleInputDialogViewController {
    @FXML
    private GridPane rootNode;
    @FXML
    private Label promptLabel;
    @FXML
    private TextField textInput;
    @FXML
    private Button submitBtn;
    @FXML
    private Label errorText;

    private Consumer<SingleInputDialogViewController> onSubmit;

    @FXML
    private void handleSubmitAction(ActionEvent actionEvent) {
        onSubmit.accept(this);
    }

    public String getInputValue() {
        return textInput.getText();
    }

    public void showError(String errorMsg) {
        errorText.setText(errorMsg);
        errorText.setVisible(true);
    }

    public Window getWindow() {
        return rootNode.getScene().getWindow();
    }

    public void close() {
        ((Stage) getWindow()).close();
    }

    static public SingleInputDialogViewController createAndShow(
            String title,
            String promptText,
            String submitBtnText,
            Consumer<SingleInputDialogViewController> onSubmit
    ) {
        return createAndShow(title, promptText, submitBtnText, onSubmit, null, null, null);
    }

    static public SingleInputDialogViewController createAndShow(
            String title,
            String promptText,
            String submitBtnText,
            Consumer<SingleInputDialogViewController> onSubmit,
            TextFormatter<?> inputTextFormatter,
            String defaultValue,
            Window owner
    ) {
        LoadedView view = View.GENERIC_SINGLE_INPUT_DIALOG.load();
        Scene scene = view.newScene();
        SingleInputDialogViewController controller = view.getLoader().getController();

        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(scene);

        controller.promptLabel.setText(promptText);
        controller.textInput.setTextFormatter(inputTextFormatter);

        if(defaultValue != null) {
            controller.textInput.setText(defaultValue);
        }
        controller.submitBtn.setText(submitBtnText);
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
