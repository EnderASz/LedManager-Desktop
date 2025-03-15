package com.enderasz.ledmanager.desktop.utils;

import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;

public class NonNegativeIntegerTextFormatter extends TextFormatter<Integer> {
    public NonNegativeIntegerTextFormatter(Integer defaultValue) {
        super(new IntegerStringConverter(), defaultValue, change -> {
            String newValue = change.getControlNewText().replaceAll("^0*", "");
            if (newValue.matches("([1-9][0-9]*)?") || newValue.isEmpty()) {
                return change;
            }
            return null;
        });
    }
}
