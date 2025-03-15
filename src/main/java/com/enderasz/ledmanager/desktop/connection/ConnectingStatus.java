package com.enderasz.ledmanager.desktop.connection;

public enum ConnectingStatus {
    NOT_INITIALIZED,
    INITIALIZATION,
    CONNECTING,
    HANDSHAKING,
    COMPATIBILITY_CHECK,
    PRECONFIGURATION,
    SUCCEEDED,
    FAILED,
    CANCELLED;
}
