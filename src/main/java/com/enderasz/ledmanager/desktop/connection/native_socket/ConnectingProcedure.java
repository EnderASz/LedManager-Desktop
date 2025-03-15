package com.enderasz.ledmanager.desktop.connection.native_socket;

import com.enderasz.ledmanager.desktop.connection.ConnectingFailedException;
import com.enderasz.ledmanager.desktop.connection.ConnectingStatus;
import javafx.beans.property.SimpleObjectProperty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

class ConnectingProcedure implements Runnable {
    private final String host;
    private final int port;
    private final SimpleObjectProperty<ConnectingStatus> status;

    private Thread threadRunningProcedure = null;

    private Socket socket = null;
    private BufferedReader socketInput = null;
    private PrintWriter socketOutput = null;

    private boolean interrupted = false;
    private Throwable failReason = null;

    public ConnectingProcedure(String host, int port, SimpleObjectProperty<ConnectingStatus> status) {
        this.host = host;
        this.port = port;
        this.status = status;
    }

    @Override
    synchronized public void run() {
        status.set(ConnectingStatus.INITIALIZATION);

        threadRunningProcedure = Thread.currentThread();
        try {
            connectionProcess();
        } catch (InterruptedException e) {
            if (!interrupted) {
                throw new RuntimeException(e);
            }
        } catch (ConnectingFailedException e) {
            setFail(e.getCause());
        }

        if (interrupted) {
            status.set(ConnectingStatus.CANCELLED);
        }
        threadRunningProcedure = null;
    }

    public void await() throws InterruptedException {
        if(threadRunningProcedure != null) {
            threadRunningProcedure.join();
        }
    }

    public void interrupt() {
        interrupted = true;

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                // Just ignore
            }
            socket = null;
        }

        threadRunningProcedure.interrupt();
        try {
            threadRunningProcedure.join(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Throwable getFailReason() {
        return failReason;
    }

    public Socket getSocket() {
        return socket;
    }

    public BufferedReader getSocketInput() {
        return socketInput;
    }

    public PrintWriter getSocketOutput() {
        return socketOutput;
    }

    private void setFail(Throwable failReason) {
        status.set(ConnectingStatus.FAILED);
        this.failReason = failReason;
    }

    private void connect() throws InterruptedException {
        status.set(ConnectingStatus.CONNECTING);
        System.out.println("Connecting to " + host + ":" + port);
        try {
            socket = new Socket(host, port);
        } catch (IllegalArgumentException e) {
            throw new ConnectingFailedException(e);
        } catch (ConnectException e) {
            throw new ConnectingFailedException(e);
        } catch (UnknownHostException e) {
            throw new ConnectingFailedException(e);
        } catch (IOException e) {
            throw new ConnectingFailedException(e);
        }

        try {
            socketOutput = new PrintWriter(socket.getOutputStream(), false);
            socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handshake() throws InterruptedException {
        status.set(ConnectingStatus.HANDSHAKING);
        socketOutput.write("H");
        socketOutput.write(0);
        socketOutput.flush();

        char[] input = new char[2];
        int readResult;
        try {
            readResult = socketInput.read(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (readResult == -1) {
            throw new RuntimeException();
        }
        if (input[0] != 'H' || input[1] != 0) {
            throw new RuntimeException();
        }
    }

    private void checkCompatibility() throws InterruptedException {
        status.set(ConnectingStatus.COMPATIBILITY_CHECK);
    }

    private void preconfigure() throws InterruptedException {
        status.set(ConnectingStatus.PRECONFIGURATION);
    }

    private void connectionProcess() throws InterruptedException {
        connect();
        handshake();
        checkCompatibility();
        preconfigure();
        status.set(ConnectingStatus.SUCCEEDED);
    }
}
