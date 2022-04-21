package gurankio.sockets;

import gurankio.sockets.protocol.Protocol;
import gurankio.sockets.protocol.ServerFacade;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class AsyncServer extends AbstractServer<AsyncHandler> {

    private final AsynchronousServerSocketChannel server;
    private final Queue<AsyncHandler> acceptQueue;

    public AsyncServer(int port, Supplier<Protocol> protocol) throws IOException {
        super(protocol);
        server = AsynchronousServerSocketChannel.open();
        server.bind(new InetSocketAddress("localhost", port));
        acceptQueue = new LinkedList<>();
    }

    @Override
    public void run() {
        tryAccept();
        super.run();
    }

    private void tryAccept() {
        server.accept(this, new CompletionHandler<AsynchronousSocketChannel, ServerFacade>() {
            @Override
            public void completed(AsynchronousSocketChannel client, ServerFacade server) {
                acceptQueue.add(new AsyncHandler(client));
                tryAccept();
            }

            @Override
            public void failed(Throwable exc, ServerFacade server) {
                tryAccept();
            }
        });
    }

    @Override
    protected boolean open() {
        return server.isOpen();
    }

    @Override
    protected Optional<AsyncHandler> accept() {
        return Optional.ofNullable(acceptQueue.poll());
    }

    @Override
    protected Stream<AsyncHandler> select() throws IOException {
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return handlers()
                .filter(h -> {
                    try {
                        return h.compute();
                    } catch (IOException e) {
                        e.printStackTrace(); // TODO: ???
                        return false;
                    }
                });
    }

    @Override
    public void close() throws IOException {
        server.close();
    }

}
