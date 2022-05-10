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

/**
 * Il compito di questa classe è quello di aprire un server a cui l'altro
 * host potrà connettersi per scambiarsi i pacchetti. Viene creato sulla macchina
 * designata per la decodifica.
 */
public class OrizzontaleDecodifica implements Orizzontale {

    private final Server server;
    private final Thread thread;

    /**
     *Il costruttore di questa classe ha il compito di creare un server a cui poi,
     * l'host in codifica, si connetterà per trasmettere i vari pacchetti.
     * @throws IOException
     */
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

    /**
     * Invia una stringa all'altro host.
     * @param string La stringa da inviare all'altro host.
     * @return "true" se l'invio è andato a buon fine, "false" in caso di errore.
     */
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

    /**
     * Interrompe l'esecuzione del thread che contiene il codice del server.
     */
    @Override
    public void close() {
        thread.interrupt();
    }

    /**
     * Questa classe contiene il codice che il server deve eseguire.
     */
    static class ProtocolloDecodifica extends Protocol {

        private LocalDateTime lastPacket;

        /**
         * Il costruttore di questa classe segnala al front-end che la connessione
         * è stata stabilita.
         */
        public ProtocolloDecodifica() {
            lastPacket = LocalDateTime.now();
            try {
                Main.VERTICALE.send("{\"type\":\"checkHorizon\", \"data\":true}");
            } catch (IOException ignored) {
            }
        }

        /**
         * Questo server funziona come una state machine che esegue sempre il metodo
         * "receive()" di questa classe.
         * @return Un oggetto "State"
         */
        @Override
        protected State connected() {
            return this::receive;
        }

        /**
         * Segnala al front-end che la connessione è caduta.
         */
        @Override
        public void disconnected() {
            try {
                Main.VERTICALE.send("{\"type\":\"checkHorizon\", \"data\":false}");
            } catch (IOException ignored) {
            }
        }

        /**
         * Questo metodo legge dal canale creato con l'altro host e invia al
         * front-end il pacchetto contenente il testo codificato.
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

        /**
         * Classico metodo getter.
         * @return Un oggetto "LocalDateTime".
         */
        public LocalDateTime getLastPacket() {
            return lastPacket;
        }
    }

    /**
     * Tipo di pacchetto contenente il testo codificato.
     */
    record EncodedText(String type, String data) {
        /**
         * Crea un record contenente il testo codificato.
         * @param data Testo codificato.
         */
        EncodedText(String data) {
            this("encodedText", data);
        }
    }
}
