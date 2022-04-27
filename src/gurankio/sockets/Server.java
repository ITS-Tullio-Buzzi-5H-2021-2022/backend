package gurankio.sockets;

import gurankio.sockets.protocol.Protocol;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class Server extends AbstractServer<Handler> {

    private final ServerSocketChannel server;
    private final Selector selector;

    public Server(int port, Supplier<Protocol> protocol) throws IOException {
        super(protocol);
        server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress("localhost", port));
        server.configureBlocking(false);
        selector = Selector.open();
    }

    @Override
    protected boolean open() {
        return server.isOpen();
    }

    @Override
    protected Optional<Handler> accept() throws IOException {
        SocketChannel client = server.accept();
        if (client != null) return Optional.of(new Handler(client, selector));
        else return Optional.empty();
    }

    protected List<Handler> select() throws IOException {
        selector.select(250);
        List<Handler> selected = selector.selectedKeys()
                .stream()
                .map(selectionKey -> (Handler) selectionKey.attachment())
                .toList();
        selector.selectedKeys().clear();
        return selected;
    }

    @Override
    public void close() throws IOException {
        server.close();
    }

}
