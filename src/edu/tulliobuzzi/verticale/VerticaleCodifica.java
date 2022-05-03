package edu.tulliobuzzi.verticale;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import edu.tulliobuzzi.Configuration;
import edu.tulliobuzzi.Main;
import edu.tulliobuzzi.algoritmo.Enigma;
import edu.tulliobuzzi.algoritmo.componenti.FabbricaRiflettori;
import edu.tulliobuzzi.algoritmo.componenti.FabbricaRotori;
import gurankio.WebSocket;
import gurankio.sockets.Server;
import gurankio.sockets.protocol.ChannelFacade;
import gurankio.sockets.protocol.Protocol;
import gurankio.sockets.protocol.ServerFacade;
import gurankio.sockets.protocol.State;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Optional;

public class VerticaleCodifica implements Verticale {

    private static final Gson GSON = new Gson();

    private final Server server;

    public VerticaleCodifica() throws IOException {
        server = new Server(
                Configuration.LOCAL_HOST,
                Configuration.ENC_WEBS_PORT,
                Encryption::new
        );
    }

    @Override
    public boolean send(String string) {
        System.out.println(string);

        for (ServerFacade.Client client : server.connected()) {
            client.channel().write(WebSocket.encode(string));
        }

        return server.connected().size() > 0;
    }

    @Override
    public void run() {
        server.run();
    }

    @Override
    public void close() throws IOException {
        server.close();
    }

    static class Encryption extends Protocol {

        private final Enigma enigma;
        private StringBuilder builder;

        public Encryption() {
            builder = new StringBuilder();
            enigma = Main.configurazioneStandard();
            System.out.println("Front-end connected.");
        }

        @Override
        protected State connected() {
            return new WebSocket(this::receive);
        }

        private State receive(ChannelFacade channel, ServerFacade server) {
            Optional<ByteBuffer> buffer = channel.poll();
            if (buffer.isEmpty()) {
                channel.read();
                return this::receive;
            }

            String json = WebSocket.decode(buffer.get());

            try {
                JsonObject packet = GSON.fromJson(json, JsonObject.class);
                System.out.println(packet);

                switch (packet.get("type").getAsString()) {
                    case "syncReflector" -> {
                        String reflector = packet.get("data").getAsString();
                        if (Objects.equals(reflector, "D")) reflector = "Default";
                        enigma.setRiflettore(FabbricaRiflettori.valueOf(reflector).build());
                    }

                    case "syncRotors" -> {
                        JsonArray rotors = packet.get("data").getAsJsonArray();
                        for (int i = 0; i < rotors.size(); i++) {
                            if (!rotors.get(i).isJsonNull()) {
                                enigma.setRotore(i, FabbricaRotori.fromJsonObject(rotors.get(i).getAsJsonObject()));
                            }
                        }
                    }

                    case "syncCables" -> {
                        String cavi = packet.get("data").getAsString();
                        enigma.setCavi(cavi);
                    }

                    case "charToEncode" -> { // 'key pressed'
                        // send to Enigma instance and buffer
                        // reply to frontend with encoded char
                        String character = packet.get("data").getAsString();
                        Enigma.Cifrazione encoded = enigma.cifra(character);
                        // DEBUG: System.out.println(character + " -> " + enigma + "\n -> " + encoded.cifrato());
                        builder.append(encoded.cifrato());
                        channel.write(WebSocket.encode(GSON.toJson(new EncodingResult(encoded.cifrato(), encoded.ruotato()))));
                    }

                    case "backspacePressed" -> {
                        builder.deleteCharAt(builder.length() - 1);
                        Boolean[] ruotato = enigma.ruotaIndietro();
                        channel.write(WebSocket.encode(GSON.toJson(new BackwardsRotation(ruotato))));
                    }

                    case "enterPressed" -> { // 'enter'
                        // tell buffer to send message to the other machine
                        try {
                            String output = builder.toString();
                            System.out.println("-> " + output);
                            builder = new StringBuilder();
                            boolean success = Main.ORIZZONTALE.send(output);
                            channel.write(WebSocket.encode("{\"type\":\"checkHorizon\", \"data\":%s}".formatted(String.valueOf(success))));
                        } catch (IOException e) {
                            // really unlikely.
                            e.printStackTrace();
                        }
                    }

                    case "checkHorizon" -> {
                        try {
                            boolean success = Main.ORIZZONTALE.send(" ".repeat(16));
                            channel.write(WebSocket.encode("{\"type\":\"checkHorizon\", \"data\":%s}".formatted(String.valueOf(success))));
                        } catch (IOException e) {
                            // really unlikely.
                            e.printStackTrace();
                        }
                    }

                    default -> {
                        System.err.printf("Invalid packet: %s%n", json);
                    }
                }
            } catch (JsonSyntaxException e) {
                System.err.printf("Invalid packet: %s%n", json);
            }

            channel.read();
            return this::receive;
        }
    }

    record EncodingResult(String type, String data, Boolean[] rotors) {
        EncodingResult(String data, Boolean[] rotors) {
            this("encodingResult", data, rotors);
        }
    }

    record BackwardsRotation(String type, Boolean[] rotors) {
        BackwardsRotation(Boolean[] rotors) {
            this("backwardsRotation", rotors);
        }
    }

}
