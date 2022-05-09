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
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * Il compito di questa classe è quello di gestire la connessione verso il front-end
 * creando un server e mettendosi in ascolto.
 */
public class VerticaleCodifica implements Verticale {

    private static final Gson GSON = new Gson();

    private final Server server;

    /**
     * Il costruttore di questa classe ha il compito di creare un server
     * per la comunicazione con il front-end.
     * @throws IOException
     */
    public VerticaleCodifica() throws IOException {
        server = new Server(
                Configuration.LOCAL_HOST,
                Configuration.ENC_WEBS_PORT,
                Encryption::new
        );
    }

    /**
     * Invia una stringa al front-end.
     * @param string La stringa da inviare al front-end.
     * @return "true" se l'invio è andato a buon fine, "false" in caso di errore.
     */
    @Override
    public boolean send(String string) {
        System.out.println(string);

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
    static class Encryption extends Protocol {

        private final Enigma enigma;
        private StringBuilder builder;
        private ByteBuffer larger;

        /**
         * Crea un oggetto "Encryption".
         */
        public Encryption() {
            builder = new StringBuilder();
            enigma = Main.configurazioneStandard();
            System.out.println("Front-end connected.");
        }

        /**
         * Crea un oggetto "WebSocket", ovvero una state machine che esegue
         * sempre il metodo "receive()" di questa classe.
         * @return Un oggetto "State".
         */
        @Override
        protected State connected() {
            return new WebSocket(this::receive);
        }

        /**
         * Questo metodo legge dal canale creato con il front-end.
         * Sulla base del tipo di messaggio inviato esegue l'operazione relativa.
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
                        System.err.printf("Unknown packet: %s%n", json);
                    }
                }
            } catch (WebSocket.ShortPacketException e) {
                larger = ByteBuffer.allocate((int) (e.getExpectedLength()) + 512).put(raw.rewind());
            } catch (JsonSyntaxException e) {
                System.err.printf("Invalid data: %s%n", Arrays.toString(raw.array()));
            }

            channel.read();
            return this::receive;
        }
    }

    /**
     * Tipo di pacchetto contenente le informazioni sui rotori e la stringa codificata.
     */
    record EncodingResult(String type, String data, Boolean[] rotors) {
        /**
         * Crea un record contenente le informazioni da inviare all'altro host.
         * @param data Stringa codificata.
         * @param rotors Rotori.
         */
        EncodingResult(String data, Boolean[] rotors) {
            this("encodingResult", data, rotors);
        }
    }

    /**
     * Tipo di pacchetto contenente le informazioni sui rotori.
     */
    record BackwardsRotation(String type, Boolean[] rotors) {
        /**
         * Crea un record contenente il settaggio dei rotori.
         * @param rotors Rotori.
         */
        BackwardsRotation(Boolean[] rotors) {
            this("backwardsRotation", rotors);
        }
    }

}
