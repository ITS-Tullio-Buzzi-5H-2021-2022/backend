package gurankio.sockets;

import gurankio.sockets.protocol.ChannelFacade;

import java.io.IOException;

/**
 * Common code for handlers.
 * Handlers abstract away any implementation difference on the underlying channel.
 *
 * @author Jacopo Del Granchio
 */
public abstract class AbstractHandler implements ChannelFacade {

    /**
     * This method is invoked by the server whenever the underlying channel has some work to do.
     *
     * @return whether this handler is still valid
     * @throws IOException whenever the underlying channel does
     */
    abstract boolean compute() throws IOException;

}
