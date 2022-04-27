package edu.tulliobuzzi.verticale;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import edu.tulliobuzzi.Configuration;
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
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class VerticaleDecodifica implements Verticale {

    private static final Gson GSON = new Gson();

    private final Server server;

    public VerticaleDecodifica() throws IOException {
        server = new Server(Configuration.DEC_WEBS_PORT, Decryption::new);
    }

    @Override
    public void send(String string) {
        String json = GSON.toJson(new EncodedText(string));
        System.out.println(json);

        for (ServerFacade.Client client : server.connected()) {
            client.channel().write(WebSocket.encode(json));
        }
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
                JsonObject packet = GSON.fromJson(json, JsonObject.class);
                System.out.println(packet);

                switch (packet.get("type").getAsString()) {
                    case "textToDecode" -> { // 'key pressed'
                        JsonArray rotorsData = packet.get("rotors").getAsJsonArray();
                        Rotore[] rotors = IntStream.range(0, rotorsData.size())
                                .mapToObj(i -> rotorsData.get(i).getAsJsonObject())
                                .map(FabbricaRotori::fromJsonObject)
                                .toArray(Rotore[]::new);
                        Enigma enigma = new Enigma(
                                FabbricaRiflettori.C.build(),
                                rotors,
                                new PannelloControllo("EF TI")
                        );

                        // send to Enigma instance and buffer
                        // reply to frontend with encoded char
                        String encodedText = packet.get("data").getAsString();
                        List<Enigma.Cifrazione> decoded = enigma.cifraStringa(encodedText);
                        // TODO: discarding rotors data as we have no animations
                        String decodedText = GSON.toJson(new DecodedText(decoded.stream()
                                .map(Enigma.Cifrazione::cifrato)
                                .collect(Collectors.joining())));
                        System.out.println(decodedText);
                        channel.write(WebSocket.encode(decodedText));
                    }
                }
            } catch (JsonSyntaxException e) {
                System.err.printf("Invalid packet: %s%n", json);
            }

            channel.read();
            return this::receive;
        }
    }

    record EncodedText(String type, String data) {
        EncodedText(String data) {
            this("encodedText", data);
        }
    }

    record DecodedText(String type, String data) {
        DecodedText(String data) {
            this("decodedText", data);
        }
    }
}
