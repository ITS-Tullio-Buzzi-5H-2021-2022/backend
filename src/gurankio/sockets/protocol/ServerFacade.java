package gurankio.sockets.protocol;

import java.util.Set;

/**
 * What is exposed to the protocol of the underlying server.
 *
 * @author Jacopo Del Granchio
 */
public interface ServerFacade {

    /**
     * Allows for inter-client communication.
     * @return the set of connected clients
     */
    Set<Client> connected();

    /**
     * A client is made of a channel and the protocol instance, or in other words its state.
     */
    record Client(ChannelFacade channel, Protocol protocol) {
    }
}
