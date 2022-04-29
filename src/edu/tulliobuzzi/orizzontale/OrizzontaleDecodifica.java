package edu.tulliobuzzi.orizzontale;

import edu.tulliobuzzi.Configuration;
import edu.tulliobuzzi.Main;
import gurankio.sockets.Server;
import gurankio.sockets.protocol.ChannelFacade;
import gurankio.sockets.protocol.Protocol;
import gurankio.sockets.protocol.ServerFacade;
import gurankio.sockets.protocol.State;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class OrizzontaleDecodifica implements Orizzontale {

    private final Thread thread;

    public OrizzontaleDecodifica() throws IOException {
        thread = new Thread(new Server(
                Configuration.PUBLIC_HOST,
                Configuration.HORIZON_PORT,
                ProtocolloDecodifica::new
        ));
        thread.setName("Horizon Server");
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public boolean send(String string) throws NotSupportedException {
        throw new NotSupportedException();
    }

    @Override
    public void close() {
        thread.interrupt();
    }

    static class ProtocolloDecodifica extends Protocol {

        @Override
        protected State connected() {
            return this::receive;
        }

        private State receive(ChannelFacade channel, ServerFacade server) {
            Optional<ByteBuffer> buffer = channel.poll();
            if (buffer.isEmpty()) {
                channel.read();
                return this::receive;
            }

            try {
                String string = String.valueOf(StandardCharsets.UTF_8.decode(buffer.get())).trim();
                if (!string.isEmpty()) {
                    System.out.println("-> " + string);
                    Main.VERTICALE.send(string);
                }
            } catch (IOException e) {
                // really unlikely.
                e.printStackTrace();
            }

            channel.read();
            return this::receive;
        }

    }

}
