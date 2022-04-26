package edu.tulliobuzzi.verticale;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
import java.util.Optional;

public class Codifica implements Verticale {

    private static final Gson GSON = new Gson();

    private final Thread thread;

    public Codifica() throws IOException {
        thread = new Thread(new Server(7000, Encryption::new));
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

        private StringBuilder builder;
        private Enigma enigma;

        public Encryption() {
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
            System.out.println(json);

            JsonObject packet = GSON.fromJson(json, JsonObject.class);

            switch (packet.get("type").getAsString()) {
                case "sync":
                    if (enigma == null) {
                        enigma = new Enigma( // TODO: configurazione
                                FabbricaRiflettori.C.build(),
                                new Rotore[]{
                                        FabbricaRotori.I.build(0, 1),
                                        FabbricaRotori.II.build(0, 2),
                                        FabbricaRotori.III.build(0, 3)
                                },
                                new PannelloControllo("EF TI")
                        );
                    }
                    // TODO: aggiorna
                    // TODO: hard sync
                    break;

                case "charToEncode": // 'key pressed'
                    // send to Enigma instance and buffer
                    // reply to frontend with encoded char
                    String character = packet.get("data").getAsString();

                    String encoded = enigma.codifica(character);
                    // TODO: get rotation
                    builder.append(encoded);
                    channel.write(WebSocket.encode(GSON.toJson(new EncodingResult(encoded, new boolean[]{false, false, false}))));
                    break;

                case "backspacePressed":
                    builder.deleteCharAt(builder.length() - 1);
                    enigma.ruotaIndietro();
                    break;

                case "enterPressed": // 'enter'
                    // tell buffer to send message to the other machine
                    try {
                        String output = builder.toString();
                        builder = new StringBuilder();
                        Main.ORIZZONTALE.send(output);
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
        EncodingResult(String data, boolean[] rotors) {
            this("encodingResult", data, rotors);
        }
    }

}
