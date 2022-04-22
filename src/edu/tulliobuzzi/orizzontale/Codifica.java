package edu.tulliobuzzi.orizzontale;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Codifica implements Orizzontale {

    private final Socket socket;
    private final DataOutputStream writer;

    public Codifica() throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress("localhost", 5000)); // TODO: "other"
        writer = new DataOutputStream(socket.getOutputStream());
    }

    public void send(String string) {
        System.out.println("-> " + string);
        try { // TODO: check if should capture.
            writer.write(StandardCharsets.UTF_8.encode(string.trim()).array());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
