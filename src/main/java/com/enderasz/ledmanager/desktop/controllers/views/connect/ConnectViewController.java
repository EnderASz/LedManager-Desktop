package com.enderasz.ledmanager.desktop.controllers.views.connect;

import com.enderasz.ledmanager.desktop.connection.*;
import com.enderasz.ledmanager.desktop.connection.native_socket.NativeSocketConnection;
import com.enderasz.ledmanager.desktop.utils.NonNegativeIntegerTextFormatter;
import com.enderasz.ledmanager.desktop.views.LoadedView;
import com.enderasz.ledmanager.desktop.views.View;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class ConnectViewController {
    @FXML
    private Label connectionError;

    @FXML
    private TextField hostInput;

    @FXML
    private TextField portInput;

    @FXML
    private SplitMenuButton connectButton;

    @FXML
    private ResourceBundle resources;

    private Consumer<ServerConnection> onSuccessfulConnect;

    @FXML
    private void initialize() {
        Platform.runLater(hostInput::requestFocus);
        portInput.setTextFormatter(new NonNegativeIntegerTextFormatter(5555));
    }

    public void registerOnSuccessfulConnect(Consumer<ServerConnection> consumer) {
        onSuccessfulConnect = consumer;
    }

    public void populateHistoricalConnections(Pair<String, Integer>[] history) {
        if (history.length > 0) {
            // We need to clear it first - because there is "no recent successful connections" item by default
            connectButton.getItems().clear();
            for (Pair<String, Integer> entry : history) {
                String host = entry.getKey();
                int port = entry.getValue();
                MenuItem item = new MenuItem(entry.getKey() + ":" + entry.getValue().toString());
                item.setOnAction(_ -> performConnect(host, port));
                connectButton.getItems().add(item);
            }

            String host = history[0].getKey();
            int port = history[0].getValue();
            hostInput.setText(host);
            portInput.setText(String.valueOf(port));
        }
    }

    private void setConnectionError(String message) {
        connectionError.setText(message);
        connectionError.setVisible(true);
    }

    private void hideConnectionError() {
        connectionError.setVisible(false);
    }

    private void performConnect(String host, Integer port) {
        hideConnectionError();

        if (host.isEmpty()) {
            setConnectionError(resources.getString("conn_err_host_empty"));
            return;
        }

        ServerConnection connection = new NativeSocketConnection(host, port);
//        ServerConnection connection = new FakeConnection(host, port);

        LoadedView dialogView = View.CONNECTING_DIALOG.load();
        Stage dialogStage = new Stage();
        Scene scene = dialogView.newScene();
        dialogStage.setScene(scene);
        dialogStage.setTitle(resources.getString("conn_dialog_title_format"));
        ConnectingDialogController connectingDialogController = dialogView.getLoader().getController();

        dialogStage.initOwner(hostInput.getScene().getWindow());
        // I use hostInput here, but it could be any element from this view
        dialogStage.initModality(Modality.WINDOW_MODAL);

        final ConnectResult connectResult = connection.connect();
        final ReadOnlyObjectProperty<ConnectingStatus> status = connectResult.getStatusProperty();

        dialogStage.setOnHidden(_ -> {
            switch (status.get()) {
                case SUCCEEDED:
                case FAILED:
                case CANCELLED:
                    return;
                default:
                    connectResult.getInterruptRunnable().run();
            }
        });
        dialogStage.setOnShown(_ -> {
            // status.set is called on non JavaFx thread so listener will also be called at non JavaFx thread.
            // We need to use Platform.runLater to schedule GUI status update within JavaFX thread
            status.addListener((_, _, _) -> Platform.runLater(() -> connectingDialogController.updateStatus(status.get())));

            connectingDialogController.updateStatus(status.get());
        });
        dialogStage.showAndWait();

        switch (status.get()) {
            case SUCCEEDED:
                onSuccessfulConnect.accept(connection);
                break;
            case FAILED:
                try {
                    throw connectResult.getFailReason();
                } catch (UnknownHostException e) {
                    setConnectionError(resources.getString("conn_err_host_unknown"));
                } catch (ConnectException e) {
                    setConnectionError(resources.getString("conn_err_connection_refused"));
                } catch (Throwable e) {
                    setConnectionError(resources.getString("conn_err_unknown"));
                    throw new RuntimeException(e);
                }
                break;
            case CANCELLED:
                return;
            default:
                throw new IllegalStateException("Unexpected status: " + status);
        }
    }

    @FXML
    private void handleConnectAction(ActionEvent actionEvent) {
        String host = hostInput.getText().strip();

        String portText = portInput.getText();
        int port;
        if (portText.isEmpty()) {
            port = 5555;
        } else {
            port = Integer.parseInt(portText);
        }

        performConnect(host, port);
    }
}
