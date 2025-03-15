package com.enderasz.ledmanager.desktop.connection.native_socket;

import com.enderasz.ledmanager.desktop.connection.ConnectResult;
import com.enderasz.ledmanager.desktop.connection.ConnectingStatus;
import com.enderasz.ledmanager.desktop.connection.ServerConnection;
import com.enderasz.ledmanager.desktop.data.config.LightConfig;
import javafx.beans.property.SimpleObjectProperty;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class NativeSocketConnection implements ServerConnection {
    private final String host;
    private final int port;

    Supplier<Socket> socketSupplier;
    Supplier<BufferedReader> readerSupplier;
    Supplier<PrintWriter> writerSupplier;

    public NativeSocketConnection(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public String getServerName() {
        return host + ":" + port;
    }

    @Override
    public ConnectResult connect() {
        SimpleObjectProperty<ConnectingStatus> status = new SimpleObjectProperty<>(ConnectingStatus.NOT_INITIALIZED);

        ConnectingProcedure procedure = new ConnectingProcedure(host, port, status);
        socketSupplier = procedure::getSocket;
        readerSupplier = procedure::getSocketInput;
        writerSupplier = procedure::getSocketOutput;

        Thread connectionThread = new Thread(procedure);

        connectionThread.start();
        return new ConnectResult(status, procedure::interrupt, procedure::getFailReason);
    }

    private void sendMessage(String message) {
        sendMessage(message.getBytes());
    }

    private void sendMessage(byte[] message) {
        PrintWriter writer = writerSupplier.get();
        for (byte b : message) {
            writer.write(b);
//            if(b > 32 && b < 127) {
//                logger.debug("Sending byte: " + BytesConverter.charToHex((char) b) + " - " + (char) b);
//            } else {
//                logger.debug("Sending byte: " + BytesConverter.charToHex((char) b));
//            }
        }
        if(message[message.length - 1] != 0) {
//            logger.debug("Sending additional null character");
            writer.write(0);
        }
        writer.flush();
    }

    @Override
    public void saveConfig(Map<Integer, LightConfig> config, String configName, UUID configId) {
        sendMessage("CFG_S");
        sendMessage(configId.toString());
        sendMessage(configName);
        sendConfig(config);
    }

    @Override
    public void uploadConfig(Map<Integer, LightConfig> config) {
        sendMessage("CFG_U");
        sendConfig(config);
    }

    private void sendConfig(Map<Integer, LightConfig> config) {
        sendMessage(String.valueOf(config.size()));
        for (Map.Entry<Integer, LightConfig> entry : config.entrySet()) {
            Integer id = entry.getKey();
            LightConfig lightConfig = entry.getValue();
            sendMessage(String.valueOf(id));
            sendMessage(lightConfig.toBytes());
        }
    }
}


