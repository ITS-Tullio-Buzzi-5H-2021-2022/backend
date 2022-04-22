package edu.tulliobuzzi.verticale;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.tulliobuzzi.Main;
import gurankio.WebSocket;
import gurankio.sockets.Server;
import gurankio.sockets.protocol.ChannelFacade;
import gurankio.sockets.protocol.Protocol;
import gurankio.sockets.protocol.ServerFacade;
import gurankio.sockets.protocol.State;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;

public class Codifica implements Verticale {

    private final Thread thread;

    public Codifica() throws IOException {
        thread = new Thread(new Server(4000, Encryption::new));
        thread.setName("WebSocket Server");
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

    static class Encryption extends Protocol {

        private final Gson gson;

        private StringBuilder builder;

        public Encryption() {
            gson = new Gson();
            builder = new StringBuilder();
        }

        @Override
        protected State connected() {
            return new WebSocket(this::receive);
        }

        private State receive(ChannelFacade channel, Optional<ServerFacade> server) {
            Optional<ByteBuffer> buffer = channel.poll();
            if (buffer.isEmpty()) {
                channel.read();
                return this::receive;
            }

            String json = WebSocket.decode(buffer.get());
            // System.out.println(json);

            JsonObject packet = gson.fromJson(json, JsonObject.class);

            switch (packet.get("type").getAsString()) {
                case "charToEncode": // 'key pressed'
                    // send to Enigma instance and buffer
                    // reply to frontend with encoded char
                    String character = packet.get("data").getAsString();

                    String encoded = Main.enigma.codifica(character);
                    builder.append(encoded);
                    channel.write(WebSocket.encode(gson.toJson(
                            new EncodingResult(
                                    "encodingResult",
                                    encoded,
                                    new boolean[]{false, false, false}
                            ))));
                    break;

                case "backspacePressed":
                    // TODO
                    break;

                case "enterPressed": // 'enter'
                    // tell buffer to send message to the other machine
                    try {
                        System.out.println(builder);
                        Main.orizzontale.send(builder.toString());
                        builder = new StringBuilder();
                    } catch (Exception e) { // TODO
                        e.printStackTrace();
                    }
                    break;
            }

            channel.read();
            return this::receive;
        }
    }

    record EncodingResult(String type, String data, boolean[] rotors) {
        //
    }

}
