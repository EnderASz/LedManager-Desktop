package com.enderasz.ledmanager.desktop.controllers.views.main.project.modes;

import com.enderasz.ledmanager.desktop.LightMode;
import com.enderasz.ledmanager.desktop.data.config.ConstLightConfig;
import com.enderasz.ledmanager.desktop.data.config.FlashingLightConfig;
import com.enderasz.ledmanager.desktop.data.config.LightConfig;


public class LightConfigViewControllerFactory {
    public static LightConfigViewController<?> create(LightMode mode) {
        switch (mode) {
            case CONST -> {
                return new ConstLightConfigViewController();
            }
            case FLASHING -> {
                return new FlashingLightConfigViewController();
            }
            default -> {
                return null;
            }
        }
    }

    public static <T extends LightConfig> LightConfigViewController<T> create(T config) {
        LightMode mode = LightMode.getForConfig(config);

        switch (mode) {
            case CONST -> {
                return (LightConfigViewController<T>) new ConstLightConfigViewController((ConstLightConfig) config);
            }
            case FLASHING -> {
                return (LightConfigViewController<T>) new FlashingLightConfigViewController((FlashingLightConfig) config);
            }
            default -> {
                return null;
            }
        }
    }
}
