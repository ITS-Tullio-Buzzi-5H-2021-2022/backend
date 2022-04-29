package edu.tulliobuzzi.orizzontale;

import edu.tulliobuzzi.Configuration;
import edu.tulliobuzzi.Main;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class OrizzontaleCodifica implements Orizzontale {

    private Socket socket;

    public OrizzontaleCodifica() {
        new Thread(this::connect).start();
    }

    private void connect() {
        int retry = 1;
        while (true) {
            try {
                synchronized (this) {
                    socket = new Socket();
                    InetSocketAddress address = new InetSocketAddress(Configuration.HORIZON_HOST, Configuration.HORIZON_PORT);
                    System.out.println(address);

                    if (address.isUnresolved()) {
                        System.out.println("Unresolved address. " + Configuration.HORIZON_HOST + ":" + Configuration.HORIZON_PORT);
                        throw new IOException();
                    }
                    socket.connect(address);
                }
                break;

            } catch (IOException e) {
                e.printStackTrace();

                synchronized (this) {
                    socket = null;
                }
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

    public synchronized boolean send(String string) throws IOException {
        if (socket == null) return false;
        try {
            OutputStream out = socket.getOutputStream();
            out.write(StandardCharsets.UTF_8.encode(string).array());
            out.flush();
            return true;
        } catch (SocketException e) {
            System.out.println("Connection to the horizon failed.");
            new Thread(this::connect).start();
            return false;
        }
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
