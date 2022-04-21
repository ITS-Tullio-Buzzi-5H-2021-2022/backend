package gurankio.sockets.protocol;

import java.nio.ByteBuffer;
import java.util.Optional;

/**
 * Interfaccia del client ai protocolli.
 */
public interface ChannelFacade {

    void read();
    Optional<ByteBuffer> poll();
    void write(ByteBuffer buffer);
    void close();

}
