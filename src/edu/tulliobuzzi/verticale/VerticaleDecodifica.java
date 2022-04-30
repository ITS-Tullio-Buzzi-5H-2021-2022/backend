package edu.tulliobuzzi.verticale;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import edu.tulliobuzzi.Configuration;
import edu.tulliobuzzi.Main;
import edu.tulliobuzzi.algoritmo.Enigma;
import edu.tulliobuzzi.algoritmo.componenti.FabbricaRiflettori;
import edu.tulliobuzzi.algoritmo.componenti.FabbricaRotori;
import edu.tulliobuzzi.algoritmo.componenti.PannelloControllo;
import edu.tulliobuzzi.algoritmo.componenti.Rotore;
import gurankio.WebSocket;
import gurankio.sockets.Server;
import gurankio.sockets.protocol.ChannelFacade;
import gurankio.sockets.protocol.Protocol;
import gurankio.sockets.protocol.ServerFacade;
import gurankio.sockets.protocol.State;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class VerticaleDecodifica implements Verticale {

    private final Server server;

    public VerticaleDecodifica() throws IOException {
        server = new Server(
                Configuration.LOCAL_HOST,
                Configuration.DEC_WEBS_PORT,
                Decryption::new
        );
    }

    @Override
    public boolean send(String string) {
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

    static class Decryption extends Protocol {

        public Decryption() {
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
                JsonObject packet = Main.GSON.fromJson(json, JsonObject.class);
                System.out.println(packet);

                switch (packet.get("type").getAsString()) {
                    case "textToDecode" -> { // 'key pressed'
                        String reflector = packet.get("reflector").getAsString();
                        if (Objects.equals(reflector, "D")) reflector = "Default";
                        JsonArray rotorsData = packet.get("rotors").getAsJsonArray();
                        List<Rotore> rotors = IntStream.range(0, rotorsData.size())
                                .mapToObj(i -> rotorsData.get(i).getAsJsonObject())
                                .map(FabbricaRotori::fromJsonObject)
                                .toList();
                        Enigma enigma = new Enigma(
                                FabbricaRiflettori.valueOf(reflector).build(),
                                rotors,
                                new PannelloControllo(packet.get("cables").getAsString())
                        );

                        // send to Enigma instance and buffer
                        // reply to frontend with encoded char
                        String encodedText = packet.get("data").getAsString();
                        List<Enigma.Cifrazione> decoded = enigma.cifraStringa(encodedText);
                        String decodedText = Main.GSON.toJson(new DecodedText(
                                decoded.stream()
                                        .map(Enigma.Cifrazione::cifrato)
                                        .collect(Collectors.joining()),
                                decoded.stream()
                                        .map(Enigma.Cifrazione::ruotato)
                                        .toArray(Boolean[][]::new)
                        ));
                        System.out.println(decodedText);
                        channel.write(WebSocket.encode(decodedText));
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
                }
            } catch (JsonSyntaxException e) {
                System.err.printf("Invalid packet: %s%n", json);
            }

            channel.read();
            return this::receive;
        }
    }

    record DecodedText(String type, String data, Boolean[][] rotations) {
        DecodedText(String data, Boolean[][] rotations) {
            this("decodedText", data, rotations);
        }
    }
}
