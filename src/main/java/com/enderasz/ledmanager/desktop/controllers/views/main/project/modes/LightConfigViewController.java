package com.enderasz.ledmanager.desktop.controllers.views.main.project.modes;

import com.enderasz.ledmanager.desktop.data.config.LightConfig;

public abstract class LightConfigViewController<T extends LightConfig> {
    protected T config;

    public LightConfigViewController() {
        super();
        config = createDefaultConfig();
    }

    public LightConfigViewController(final T config) {
        this.config = config;
    }

    protected abstract T createDefaultConfig();

    public T getLightConfig() {
        return config;
    }
}
