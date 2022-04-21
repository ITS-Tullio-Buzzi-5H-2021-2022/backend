package gurankio.sockets;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncHandler extends AbstractHandler {

    private final AsynchronousSocketChannel channel;
    private final Queue<ByteBuffer> reads;
    private final AtomicInteger pending;

    public AsyncHandler(AsynchronousSocketChannel channel) {
        super();
        this.channel = channel;
        this.reads = new LinkedList<>();
        this.pending = new AtomicInteger(0);
    }

    @Override
    boolean compute() throws IOException {
        return pending.get() == 0;
    }

    @Override
    public void read() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        pending.incrementAndGet();
        channel.read(buffer, null, new CompletionHandler<>() {
            @Override
            public void completed(Integer bytes, Object attachment) {
                if (bytes == -1) {
                    close();
                } else {
                    reads.add(buffer.flip());
                }
                pending.decrementAndGet();
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                pending.decrementAndGet();
            }
        });
    }

    @Override
    public Optional<ByteBuffer> poll() {
        return Optional.ofNullable(reads.poll());
    }

    @Override
    public void write(ByteBuffer buffer) {
        pending.incrementAndGet();
        channel.write(buffer, null, new CompletionHandler<>() {
            @Override
            public void completed(Integer result, Object attachment) {
                pending.decrementAndGet();
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                pending.decrementAndGet();
            }
        });
    }

    @Override
    public void close() {
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
