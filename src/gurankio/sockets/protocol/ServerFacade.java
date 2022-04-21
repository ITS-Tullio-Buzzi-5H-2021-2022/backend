package gurankio.sockets.protocol;

import java.util.Set;

/**
 * Interfaccia del server ai protocolli.
 */
public interface ServerFacade {

    Set<Client> connected();

    record Client(ChannelFacade channel, Protocol protocol) {
    }
}
