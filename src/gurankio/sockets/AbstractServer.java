package gurankio.sockets;

import gurankio.sockets.protocol.Protocol;
import gurankio.sockets.protocol.ServerFacade;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Common code for server implementations.
 *
 * @author Jacopo Del Granchio
 */
public abstract class AbstractServer<H extends AbstractHandler> implements ServerFacade, Closeable, Runnable {

    /**
     * A protocol supplier, usually the constructor.
     * Passing a supplier around removes any need for reflection and non type checked parameters,
     * making the class safer to use.
     */
    private final Supplier<Protocol> protocol;

    /**
     * Maps between a valid handler and its state.
     */
    private final Map<H, Protocol> handlers;

    public AbstractServer(Supplier<Protocol> protocol) {
        this.protocol = protocol;
        this.handlers = new ConcurrentHashMap<>();
    }

    /**
     * @return whether the server is still open
     */
    protected abstract boolean open();

    /**
     * Accepts a new connection.
     *
     * @return the new handler to be registered
     * @throws IOException whenever some error is encountered while accepting a new connection
     */
    protected abstract Optional<H> accept() throws IOException;

    /**
     * Selects all clients to process.
     *
     * @return the list of clients to process
     * @throws IOException whenever some error is encountered while selecting the clients
     */
    protected abstract List<H> select() throws IOException;

    /**
     * Utility method to advance a handler state if it exists otherwise create it.
     * @param h the handler for which to advance the state
     */
    private void compute(H h) {
        handlers.compute(h, (i, p) -> p == null ? protocol.get() : p.advance(h, this));
    }

    /**
     * The server loop implemented as A {@link Runnable} for ease of use with {@link Thread}.
     *
     * @implNote
     * A standard implementation of a server loop.
     * Highly opinionated.
     */
    @Override
    public void run() {
        try (this) {
            while (open()) {
                accept().ifPresent(this::compute);
                for (H h : select()) {
                    try {
                        if (h.compute()) {
                            compute(h);
                        } else {
                            handlers.remove(h);
                            h.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("Removing client.");
                        handlers.remove(h);
                        h.close();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Set<Client> connected() {
        return handlers.entrySet()
                .stream()
                .map(entry -> new Client(entry.getKey(), entry.getValue()))
                .collect(Collectors.toUnmodifiableSet());
    }

}
