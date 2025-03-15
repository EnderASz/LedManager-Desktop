package com.enderasz.ledmanager.desktop.controllers.views.main.project.modes;

import com.enderasz.ledmanager.desktop.data.config.ConstLightConfig;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

public class ConstLightConfigViewController extends LightConfigViewController<ConstLightConfig> {
    @FXML
    private ColorPicker lightColorPicker;

    public ConstLightConfigViewController() {
        super();
    }

    public ConstLightConfigViewController(final ConstLightConfig config) {
        super(config);
    }

    @FXML
    private void initialize() {
        lightColorPicker.setValue(config.getLightColor());
    }

    @Override
    protected ConstLightConfig createDefaultConfig() {
        return new ConstLightConfig();
    }

    @FXML
    private void handleColorChange(ActionEvent actionEvent) {
        Color newColor = lightColorPicker.getValue();
        getLightConfig().setLightColor(newColor);
    }
}
