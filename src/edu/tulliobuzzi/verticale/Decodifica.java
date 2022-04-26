package edu.tulliobuzzi.verticale;

import gurankio.WebSocket;
import gurankio.sockets.Server;
import gurankio.sockets.protocol.ChannelFacade;
import gurankio.sockets.protocol.Protocol;
import gurankio.sockets.protocol.ServerFacade;
import gurankio.sockets.protocol.State;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class Decodifica implements Verticale {

    private final Server server;
    private final Thread thread;

    public Decodifica() throws IOException {
        server = new Server(42069, Decryption::new); // TODO: porta 6000?
        thread = new Thread(server); // TODO: data races?
        thread.setName("WebSocket Server");
        thread.setDaemon(true);
        thread.start();
    }

    public void send(String string) {
        server.connected()
                .stream()
                .findAny()
                .orElseThrow()
                .channel()
                .write(WebSocket.encode(string));
    }

    @Override
    public void close() throws IOException {
        thread.interrupt();
    }

    static class Decryption extends Protocol {

        @Override
        protected State connected() {
            return new WebSocket(this::noop);
        }

        private State noop(ChannelFacade channel, Optional<ServerFacade> server) {
            return this::noop;
        }
    }
}
