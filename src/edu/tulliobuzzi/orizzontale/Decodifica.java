package edu.tulliobuzzi.orizzontale;

import edu.tulliobuzzi.Main;
import gurankio.sockets.Server;
import gurankio.sockets.protocol.ChannelFacade;
import gurankio.sockets.protocol.Protocol;
import gurankio.sockets.protocol.ServerFacade;
import gurankio.sockets.protocol.State;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class Decodifica implements Orizzontale {

    private final Thread thread;

    public Decodifica() throws IOException {
        thread = new Thread(new Server(8000, Decryption::new));
        thread.setName("Horizon Server");
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void send(String string) throws Exception {
        throw new Exception("Not a valid operation.");
    }

    @Override
    public void close() throws IOException {
        thread.interrupt();
    }

    static class Decryption extends Protocol {

        @Override
        protected State connected() {
            return this::receive;
        }

        private State receive(ChannelFacade channel, Optional<ServerFacade> server) {
            Optional<ByteBuffer> buffer = channel.poll();

            if (buffer.isEmpty()) {
                channel.read();
                return this::receive;
            }

            try {
                String string = String.valueOf(StandardCharsets.UTF_8.decode(buffer.get()));
                Main.VERTICALE.send(string);
            } catch (Exception e) { // TODO
                e.printStackTrace();
            }

            channel.read();
            return this::receive;
        }

    }

}
