package com.enderasz.ledmanager.desktop.connection;

import com.enderasz.ledmanager.desktop.data.config.LightConfig;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Map;
import java.util.UUID;

public class FakeConnection implements ServerConnection {
    private String host;
    private int port;

    private final SimpleObjectProperty<ConnectingStatus> status = new SimpleObjectProperty<>(ConnectingStatus.NOT_INITIALIZED);

    public FakeConnection(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public String getServerName() {
        return host + ":" + port;
    }

    @Override
    public ConnectResult connect() {
        status.set(ConnectingStatus.INITIALIZATION);
        Thread connectionThread = new Thread(() -> {
            try {
                fakeConnectionProcess();
            } catch (InterruptedException e) {
                status.set(ConnectingStatus.CANCELLED);
            }
        });

        connectionThread.start();
        return new ConnectResult(status, () -> {
            connectionThread.interrupt();
            try {
                connectionThread.join(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, this::getFailReason);
    }

    private Exception getFailReason() {
        return null;
    }

    private void fakeConnectionProcess() throws InterruptedException {
        Thread.sleep(100);
        status.set(ConnectingStatus.CONNECTING);
        Thread.sleep(1000);
        status.set(ConnectingStatus.HANDSHAKING);
        Thread.sleep(1000);
        status.set(ConnectingStatus.COMPATIBILITY_CHECK);
        Thread.sleep(1000);
        status.set(ConnectingStatus.PRECONFIGURATION);
        Thread.sleep(1000);

        status.set(ConnectingStatus.SUCCEEDED);
    }

    @Override
    public void saveConfig(Map<Integer, LightConfig> config, String configName, UUID configId) {}

    public void uploadConfig(Map<Integer, LightConfig> config) {}
}
