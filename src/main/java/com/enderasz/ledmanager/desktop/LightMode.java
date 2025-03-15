package com.enderasz.ledmanager.desktop;

import com.enderasz.ledmanager.desktop.data.config.ConstLightConfig;
import com.enderasz.ledmanager.desktop.data.config.FlashingLightConfig;
import com.enderasz.ledmanager.desktop.data.config.LightConfig;
import com.enderasz.ledmanager.desktop.data.config.NoneLightConfig;
import com.enderasz.ledmanager.desktop.views.View;

import java.util.Arrays;
import java.util.ResourceBundle;

public enum LightMode {
    NONE("light_mode_none", null, NoneLightConfig.class),
    CONST("light_mode_const", View.MAIN_PROJECT_CONST_LIGHT_CONF_SUBVIEW, ConstLightConfig.class),
    FLASHING("light_mode_flashing", View.MAIN_PROJECT_FLASHING_LIGHT_CONF_SUBVIEW, FlashingLightConfig.class);

    private final String translationKey;
    private final View view;
    private final Class<? extends LightConfig> configCls;

    LightMode(String translationKey, View view, Class<? extends LightConfig> configCls) {
        this.translationKey = translationKey;
        this.view = view;
        this.configCls = configCls;
    }

    public String getDisplayNameFor(ResourceBundle translations) {
        return translations.getString(translationKey);
    }

    public View getView() {
        return view;
    }

    public static LightMode getByDisplayName(ResourceBundle translations, String displayName) {
        return Arrays.stream(
                LightMode.values()
        ).filter(
                (LightMode mode) -> mode.getDisplayNameFor(translations).equals(displayName)
        ).findFirst().orElse(null);
    }

    public static LightMode getByConfigCls(Class<? extends LightConfig> configCls) {
        return Arrays.stream(
                LightMode.values()
        ).filter(
                (LightMode mode) -> mode.configCls == configCls
        ).findFirst().orElse(null);
    }

    public static LightMode getForConfig(LightConfig config) {
        if(config == null) {
            return getByConfigCls(null);
        }
        return getByConfigCls(config.getClass());
    }
}
