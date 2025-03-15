package com.enderasz.ledmanager.desktop;

import com.enderasz.ledmanager.desktop.connection.ServerConnection;
import com.enderasz.ledmanager.desktop.controllers.views.connect.ConnectViewController;
import com.enderasz.ledmanager.desktop.views.LoadedView;
import com.enderasz.ledmanager.desktop.views.View;
import com.enderasz.ledmanager.desktop.controllers.views.main.MainViewController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;


public class MainApplication extends Application {
    private Stage connectViewStage;
    private Stage mainViewStage;

    private void initializeConnectView() {
        if(connectViewStage != null) {
            // Only one connect view can be opened at once
            throw new RuntimeException("ConnectView is already initialized");
        }

        LoadedView view = View.CONNECT.load();
        Scene scene = view.newScene();
        ConnectViewController controller = view.getLoader().getController();
        controller.populateHistoricalConnections(new Pair[]{});

        controller.registerOnSuccessfulConnect(this::onSuccessfulConnect);

        connectViewStage = new Stage();
        connectViewStage.setScene(scene);
        connectViewStage.setResizable(false);
        connectViewStage.setTitle("LedManager - Connect");
        connectViewStage.show();
    }

    private void closeConnectView() {
        if(connectViewStage == null) {
            throw new RuntimeException("ConnectView stage is not initialized");
        }

        connectViewStage.close();
        connectViewStage = null;
    }

    private void initializeMainView(ServerConnection connection) {
        if(mainViewStage != null) {
            // Only one main view can be opened at once
            //  Close previous if new one is requested
            mainViewStage.close();
        }

        LoadedView view = View.MAIN.load();
        Scene scene = view.newScene();
        MainViewController controller = view.getLoader().getController();

        controller.setConnection(connection);

        mainViewStage = new Stage();
        mainViewStage.setScene(scene);
        mainViewStage.setResizable(false);
        controller.updateWindowTitle();

        mainViewStage.show();
    }

    private void onSuccessfulConnect(ServerConnection connection) {
        closeConnectView();
        initializeMainView(connection);
    }

    @Override
    public void start(Stage stage) {
        initializeConnectView();
    }

    public static void main(String[] args) {
        launch();
    }
}