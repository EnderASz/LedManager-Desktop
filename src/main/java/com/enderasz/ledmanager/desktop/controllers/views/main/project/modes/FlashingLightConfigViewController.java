package com.enderasz.ledmanager.desktop.controllers.views.main.project.modes;

import com.enderasz.ledmanager.desktop.data.config.FlashingLightConfig;
import com.enderasz.ledmanager.desktop.utils.NonNegativeIntegerTextFormatter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.paint.Color;
import javafx.util.converter.IntegerStringConverter;

public class FlashingLightConfigViewController extends LightConfigViewController<FlashingLightConfig> {
    @FXML
    private ColorPicker lightColorPicker;

    @FXML
    private TextField timeLengthInput;

    @FXML
    private TextField timeIntervalInput;

    public FlashingLightConfigViewController() {
        super();
    }

    public FlashingLightConfigViewController(final FlashingLightConfig config) {
        super(config);
    }

    @Override
    protected FlashingLightConfig createDefaultConfig() {
        return new FlashingLightConfig();
    }

    @FXML
    private void initialize() {
        lightColorPicker.setValue(config.getLightColor());

        timeLengthInput.setTextFormatter(new NonNegativeIntegerTextFormatter(config.getTimeLength()));
        timeIntervalInput.setTextFormatter(new NonNegativeIntegerTextFormatter(config.getTimeInterval()));

        timeLengthInput.textProperty().addListener((observable, oldValue, newValue) -> config.setTimeLength(!newValue.isEmpty() ? Integer.parseInt(newValue) : 0));
        timeIntervalInput.textProperty().addListener((observable, oldValue, newValue) -> config.setTimeInterval(!newValue.isEmpty() ? Integer.parseInt(newValue) : 0));
    }

    @FXML
    private void handleColorChange(ActionEvent actionEvent) {
        Color newColor = lightColorPicker.getValue();
        getLightConfig().setLightColor(newColor);
    }
}
