package edu.tulliobuzzi.orizzontale;

import edu.tulliobuzzi.Configuration;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class OrizzontaleCodifica implements Orizzontale {

    private Socket socket;

    public OrizzontaleCodifica() {
        connect();
    }

    private synchronized void connect() {
        int retry = 1;
        while (true) {
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(Configuration.HORIZON_HOST, Configuration.HORIZON_PORT));
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
        try {
            OutputStream out = socket.getOutputStream();
            out.write(StandardCharsets.UTF_8.encode(string).array());
            out.flush();
        } catch (SocketException e) {
            System.out.println("Connection to the horizon failed.");
            connect();
            send(string);
        }
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
