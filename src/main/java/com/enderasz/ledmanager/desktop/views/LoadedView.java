package com.enderasz.ledmanager.desktop.views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoadedView {
    private final FXMLLoader fxmlLoader;
    private final Parent node;

    public LoadedView(URL url, ResourceBundle resourceBundle) {
        this(url, resourceBundle, null);
    }

    public LoadedView(URL url, ResourceBundle resourceBundle, Callback<Class<?>, Object> controllerFactory) {
        fxmlLoader = new FXMLLoader(url, resourceBundle);
        if(controllerFactory != null) {
            fxmlLoader.setControllerFactory(controllerFactory);
        }
        try {
            node = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FXMLLoader getLoader() {
        return fxmlLoader;
    }

    public Node getNode() {
        return node;
    }

    public Scene newScene() {
        return new Scene(node);
    }
}
