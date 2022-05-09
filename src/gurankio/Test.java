package gurankio;

import gurankio.sockets.Server;
import gurankio.sockets.protocol.ChannelFacade;
import gurankio.sockets.protocol.Protocol;
import gurankio.sockets.protocol.ServerFacade;
import gurankio.sockets.protocol.State;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;

/**
 * Awful test that makes an echo websocket server on port 8080.
 */
public class Test {

    public static void main(String[] args) throws IOException {
        new Server("localhost", 8080, Echo::new).run();
    }

    public static class Echo extends Protocol {

        @Override
        public State connected() {
            return new WebSocket(this::echo);
        }

        private State echo(ChannelFacade channel, ServerFacade server) {
            Optional<ByteBuffer> encoded = channel.poll();
            if (encoded.isPresent()) {
                String message = WebSocket.decode(encoded.get());
                System.out.println("ECHO: " + message);
                channel.write(WebSocket.encode(message));
            }
            channel.read();
            return this::echo;
        }
    }
}
