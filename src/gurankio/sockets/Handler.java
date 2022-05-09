package gurankio.sockets;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

/**
 * An implementation of a handler which uses a {@link SelectionKey} to handle multiple clients on a single thread.
 *
 * @author Jacopo Del Granchio
 * @see Server
 */
public class Handler extends AbstractHandler {

    private final SocketChannel channel;
    private final SelectionKey key;
    private final Queue<ByteBuffer> reads;
    private final Queue<ByteBuffer> writes;

    public Handler(SocketChannel channel, Selector selector) throws IOException {
        this.channel = channel;
        this.channel.configureBlocking(false);
        this.key = this.channel.register(selector, 0);
        this.key.attach(this);
        this.reads = new LinkedList<>();
        this.writes = new LinkedList<>();
    }

    @Override
    boolean compute() throws IOException {
        if (key.isValid() && key.isReadable()) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int bytes = channel.read(buffer);
            if (bytes == -1) {
                close();
                return false;
            }
            reads.add(buffer.flip());
            key.interestOpsAnd(~SelectionKey.OP_READ);
        }
        if (key.isValid() && key.isWritable()) {
            if (!writes.isEmpty()) {
                for (ByteBuffer buffer : writes) channel.write(buffer);
                writes.clear();
            }
            key.interestOpsAnd(~SelectionKey.OP_WRITE);
        }
        return key.isValid();
    }

    @Override
    public void read() {
        key.interestOpsOr(SelectionKey.OP_READ);
    }

    @Override
    public Optional<ByteBuffer> poll() {
        return Optional.ofNullable(reads.poll());
    }

    @Override
    public void write(ByteBuffer buffer) {
        writes.add(buffer);
        key.interestOpsOr(SelectionKey.OP_WRITE);
    }

    @Override
    public void close() {
        try {
            key.cancel();
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
