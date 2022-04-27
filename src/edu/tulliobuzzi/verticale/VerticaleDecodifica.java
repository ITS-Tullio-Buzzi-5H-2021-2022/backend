package edu.tulliobuzzi.verticale;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
            System.out.println(json);

            JsonObject packet = GSON.fromJson(json, JsonObject.class);

            switch (packet.get("type").getAsString()) {
                case "textToDecode": // 'key pressed'
                    Enigma enigma = new Enigma( // TODO: configurazione
                            FabbricaRiflettori.C.build(),
                            new Rotore[]{
                                    FabbricaRotori.I.build(0, 1),
                                    FabbricaRotori.II.build(0, 2),
                                    FabbricaRotori.III.build(0, 3)
                            },
                            new PannelloControllo("EF TI")
                    );

                    // send to Enigma instance and buffer
                    // reply to frontend with encoded char
                    String string = packet.get("data").getAsString();
                    List<Enigma.Cifrazione> decoded = enigma.cifraStringa(string);
                    // TODO: discarding rotors data as we have no animations
                    channel.write(WebSocket.encode(GSON.toJson(
                            new DecodedText(decoded.stream()
                                    .map(Enigma.Cifrazione::cifrata)
                                    .collect(Collectors.joining())
                            ))));
                    break;
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
