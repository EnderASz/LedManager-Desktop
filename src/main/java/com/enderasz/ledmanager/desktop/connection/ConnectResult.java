package com.enderasz.ledmanager.desktop.connection;

import javafx.beans.property.ReadOnlyObjectProperty;

import java.util.function.Supplier;

public class ConnectResult {
    private final ReadOnlyObjectProperty<ConnectingStatus> status;
    private final Runnable interruptRunnable;
    private final Supplier<Throwable> failReasonSupplier;

    public ConnectResult(ReadOnlyObjectProperty<ConnectingStatus> status, Runnable interruptRunnable, Supplier<Throwable> failReasonSupplier) {
        this.status = status;
        this.interruptRunnable = interruptRunnable;
        this.failReasonSupplier = failReasonSupplier;
    }

    public ReadOnlyObjectProperty<ConnectingStatus> getStatusProperty() {
        return status;
    }

    public Runnable getInterruptRunnable() {
        return interruptRunnable;
    }

    public Throwable getFailReason() {
        return failReasonSupplier.get();
    }

}
