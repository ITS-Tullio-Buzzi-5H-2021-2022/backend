package gurankio.sockets;

import gurankio.sockets.protocol.ChannelFacade;
import gurankio.sockets.protocol.Protocol;
import gurankio.sockets.protocol.ServerFacade;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.function.Supplier;

public class SingleServer implements ChannelFacade, ServerFacade, Runnable, Closeable {

    private final ServerSocketChannel server;
    private final Protocol protocol;
    private final Queue<ByteBuffer> reads;

    private SocketChannel channel;

    public SingleServer(int port, Supplier<Protocol> protocol) throws IOException {
        this.server = ServerSocketChannel.open();
        this.server.bind(new InetSocketAddress(port));
        this.protocol = protocol.get().advance(this, this);
        this.reads = new LinkedList<>();
    }

    @Override
    public void read() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try {
            channel.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<ByteBuffer> poll() {
        return Optional.ofNullable(reads.poll());
    }

    @Override
    public void write(ByteBuffer buffer) {
        try {
            channel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try (this) {
            channel = server.accept();
            while (channel.isOpen()) {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Set<Client> connected() {
        return Set.of(new Client(this, protocol));
    }
}
