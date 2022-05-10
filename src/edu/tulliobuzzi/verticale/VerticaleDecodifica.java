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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class VerticaleDecodifica implements Verticale {

    private final Server server;

    /**
     * Avvia un server a cui il front-end dovrà connettersi per leggere
     * le informazioni decodificate.
     * @throws IOException
     */
    public VerticaleDecodifica() throws IOException {
        server = new Server(
                Configuration.LOCAL_HOST,
                Configuration.DEC_WEBS_PORT,
                Decryption::new
        );
    }

    /**
     * Invia una stringa al front-end.
     * @param string La stringa da inviare al front-end.
     * @return "true" se l'invio è andato a buon fine, "false" in caso di errore.
     */
    @Override
    public boolean send(String string) {
        for (ServerFacade.Client client : server.connected()) {
            client.channel().write(WebSocket.encode(string));
        }

        return server.connected().size() > 0;
    }

    /**
     * Avvia il server.
     */
    @Override
    public void run() {
        server.run();
    }

    /**
     * Chiude il server.
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        server.close();
    }

    /**
     * Questa classe contiene il codice che il server deve eseguire.
     */
    static class Decryption extends Protocol {

        private ByteBuffer larger;

        /**
         * Crea un oggetto "Decryption".
         */
        public Decryption() {
            System.out.println("Front-end connected.");
        }

        /**
         * Crea un oggetto "WebSocket", ovvero una state machine che esegue sempre il metodo
         * "receive()" di questa classe.
         * @return Un oggetto "State".
         */
        @Override
        protected State connected() {
            return new WebSocket(this::receive);
        }

        /**
         * Questo metodo si occupa di leggere dal canale il testo, decodificarlo,
         * e solo alla fine inviarlo al front-end.
         * @param channel Il canale con cui operare.
         * @param server L'oggetto server su cui viene eseguito questo codice.
         * @return Un oggetto "State".
         */
        private State receive(ChannelFacade channel, ServerFacade server) {
            Optional<ByteBuffer> buffer = channel.poll();
            if (buffer.isEmpty()) {
                channel.read();
                return this::receive;
            }

            ByteBuffer raw = larger == null ? buffer.get() : larger.put(buffer.get()).flip();

            try {
                String json = WebSocket.decode(raw);
                larger = null;
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

                    default -> {
                        System.err.printf("Unknown packet: %s%n", json);
                    }
                }
            } catch (WebSocket.ShortPacketException e) {
                larger = ByteBuffer.allocate((int) (e.getExpectedLength()) + 512).put(raw.rewind());
            }  catch (JsonSyntaxException e) {
                System.err.printf("Invalid data: %s%n", Arrays.toString(raw.array()));
            }

            channel.read();
            return this::receive;
        }
    }

    /**
     * Tipo di pacchetto contenente il testo decodificato.
     */
    record DecodedText(String type, String data, Boolean[][] rotations) {
        /**
         * Crea un record contenente il testo decodificato e le informazioni sui rotori.
         * @param data Testo decodificato.
         * @param rotations Rotori.
         */
        DecodedText(String data, Boolean[][] rotations) {
            this("decodedText", data, rotations);
        }
    }
}
