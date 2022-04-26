package edu.tulliobuzzi.orizzontale;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class Codifica implements Orizzontale {

    private Socket socket;

    public Codifica() {
        connect();
    }

    private synchronized void connect() {
        int retry = 1;
        while (true) {
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress("localhost", 8000)); // TODO: "other"
                break;
            } catch (IOException e) {
                try {
                    System.out.printf("Retrying connection to the horizon. (%d)%n", retry);
                    Thread.onSpinWait();
                    Thread.sleep((long) Math.min(30 * 1000, 100 * Math.pow(2, retry++)));
                } catch (InterruptedException i) {
                    return;
                }
            }
        }
        System.out.println("Connected to the horizon.");
    }

    public synchronized void send(String string) throws IOException {
        try { // TODO: check if should capture.
            socket.getOutputStream().write(StandardCharsets.UTF_8.encode(string).array());
            socket.getOutputStream().flush();
        } catch (SocketException e) {
            connect();
            // TODO: send(string);
        }
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
