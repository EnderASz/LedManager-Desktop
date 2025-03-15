package com.enderasz.ledmanager.desktop.connection;

import com.enderasz.ledmanager.desktop.data.config.LightConfig;

import java.util.Map;
import java.util.UUID;

public interface ServerConnection {
    String getServerName();

    ConnectResult connect();

    void saveConfig(Map<Integer, LightConfig> config, String configName, UUID configId);

    void uploadConfig(Map<Integer, LightConfig> config);
}
