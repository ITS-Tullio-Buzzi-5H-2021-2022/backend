package gurankio;

import gurankio.sockets.protocol.ChannelFacade;
import gurankio.sockets.protocol.Protocol;
import gurankio.sockets.protocol.ServerFacade;
import gurankio.sockets.protocol.State;

import java.nio.ByteBuffer;
import java.util.Optional;

public class Echo extends Protocol {

    @Override
    public State connected() {
        return new WebSocket(this::echo);
    }

    private State echo(ChannelFacade channel, Optional<ServerFacade> server) {
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
