package com.enderasz.ledmanager.desktop.views;

import com.enderasz.ledmanager.desktop.ApplicationPreferences;
import javafx.util.Callback;

import java.net.URL;

public enum View {
    GENERIC_SINGLE_INPUT_DIALOG("common/single_input_dialog.fxml"),
    GENERIC_CONFIRM_DIALOG("common/confirm_dialog.fxml"),


    CONNECT("connect/root.fxml"),
    CONNECTING_DIALOG("connect/connecting_dialog.fxml"),

    MAIN("main/root.fxml"),

    MAIN_PROJECT_TAB_SUBVIEW("main/project/root.fxml"),
    MAIN_PROJECT_CONST_LIGHT_CONF_SUBVIEW("main/project/modes/constant.fxml"),
    MAIN_PROJECT_FLASHING_LIGHT_CONF_SUBVIEW("main/project/modes/flashing.fxml");

    private final URL url;

    View(String fileName) {
        URL resource = this.getClass().getResource(fileName);
        if(resource == null) {
            throw new RuntimeException("View resource not found: " + fileName);
        }
        this.url = resource;
    }

    public URL getUrl() {
        return url;
    }

    public LoadedView load() {
        return load(null);
    }

    public LoadedView load(Callback<Class<?>, Object> controllerFactory) {
        return new LoadedView(getUrl(), ApplicationPreferences.getInstance().getLanguageBundle(), controllerFactory);
    }
}
