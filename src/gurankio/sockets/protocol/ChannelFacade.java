package gurankio.sockets.protocol;

import java.io.Closeable;
import java.nio.ByteBuffer;
import java.util.Optional;

/**
 * Interfaccia del client ai protocolli.
 */
public interface ChannelFacade extends Closeable {

    void read();
    Optional<ByteBuffer> poll();
    void write(ByteBuffer buffer);
    void close();

}
