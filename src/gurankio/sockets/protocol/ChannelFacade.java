package gurankio.sockets.protocol;

import java.io.Closeable;
import java.nio.ByteBuffer;
import java.util.Optional;

/**
 * What is exposed to the protocol of the underlying channel.
 *
 * @author Jacopo Del Granchio
 */
public interface ChannelFacade extends Closeable {

    /**
     * Marks this channel for reading.
     */
    void read();

    /**
     * Polls the input stream for any data.
     * The underlying server shall do its best to make sure that a protocol will be updated
     * only and only if it received data, but it is not guaranteed.
     *
     * @return the data received
     */
    Optional<ByteBuffer> poll();

    /**
     * Marks this channel for writing and pushes a {@link ByteBuffer} to the writes queue.
     * The underlying server shall do its best to make sure that a protocol will be updated
     * only and only if all the requested writes have been performed, but it is not guaranteed.
     *
     * @param buffer the buffer to write.
     */
    void write(ByteBuffer buffer);

    /**
     * Marks this channel as closed and that cleanup may be started.
     */
    void close();

}
