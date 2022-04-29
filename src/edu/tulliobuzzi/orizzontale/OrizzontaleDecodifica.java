package edu.tulliobuzzi.orizzontale;

import edu.tulliobuzzi.Configuration;
import edu.tulliobuzzi.Main;
import edu.tulliobuzzi.verticale.VerticaleDecodifica;
import gurankio.sockets.Server;
import gurankio.sockets.protocol.ChannelFacade;
import gurankio.sockets.protocol.Protocol;
import gurankio.sockets.protocol.ServerFacade;
import gurankio.sockets.protocol.State;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class OrizzontaleDecodifica implements Orizzontale {

    private final Server server;
    private final Thread thread;

    public OrizzontaleDecodifica() throws IOException {
        server = new Server(
                Configuration.PUBLIC_HOST,
                Configuration.HORIZON_PORT,
                ProtocolloDecodifica::new
        );
        thread = new Thread(server);
        thread.setName("Horizon Server");
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public boolean send(String string) {
        for (ServerFacade.Client client : server.connected()) {
            client.channel().write(StandardCharsets.UTF_8.encode(string));
        }

        return server.connected()
                .stream()
                .map(ServerFacade.Client::protocol)
                .map(p -> (ProtocolloDecodifica) p)
                .map(ProtocolloDecodifica::getLastPacket)
                .map(time -> ChronoUnit.MINUTES.between(time, LocalDateTime.now()))
                .anyMatch(ago -> ago < 2);
    }

    @Override
    public void close() {
        thread.interrupt();
    }

    static class ProtocolloDecodifica extends Protocol {

        private LocalDateTime lastPacket;

        public ProtocolloDecodifica() {
            lastPacket = LocalDateTime.now();
            try {
                Main.VERTICALE.send("{\"type\":\"checkHorizon\", \"data\":true}");
            } catch (IOException ignored) {
            }
        }

        @Override
        protected State connected() {
            return this::receive;
        }

        @Override
        public void disconnected() {
            try {
                Main.VERTICALE.send("{\"type\":\"checkHorizon\", \"data\":false}");
            } catch (IOException ignored) {
            }
        }

        private State receive(ChannelFacade channel, ServerFacade server) {
            Optional<ByteBuffer> buffer = channel.poll();
            if (buffer.isEmpty()) {
                channel.read();
                return this::receive;
            }
            lastPacket = LocalDateTime.now();
            try {
                String string = String.valueOf(StandardCharsets.UTF_8.decode(buffer.get())).trim();
                if (!string.isEmpty()) {
                    System.out.println("-> " + string);
                    String json = Main.GSON.toJson(new EncodedText(string));
                    Main.VERTICALE.send(json);
                }
            } catch (IOException e) {
                // really unlikely.
                e.printStackTrace();
            }

            channel.read();
            return this::receive;
        }

        public LocalDateTime getLastPacket() {
            return lastPacket;
        }
    }

    record EncodedText(String type, String data) {
        EncodedText(String data) {
            this("encodedText", data);
        }
    }
}
