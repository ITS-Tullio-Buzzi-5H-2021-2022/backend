package gurankio.sockets;

import gurankio.sockets.protocol.ChannelFacade;

import java.io.IOException;

public abstract class AbstractHandler implements ChannelFacade {

    abstract boolean compute() throws IOException;

}
