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

public abstract class AbstractServer<H extends AbstractHandler> implements ServerFacade, Closeable, Runnable {

    private final Supplier<Protocol> protocol;
    private final Map<H, Protocol> handlers;

    public AbstractServer(Supplier<Protocol> protocol) {
        this.protocol = protocol;
        this.handlers = new ConcurrentHashMap<>();
    }

    protected abstract boolean open();

    protected abstract Optional<H> accept() throws IOException;

    protected abstract List<H> select() throws IOException;

    @Override
    public void run() {
        try (this) {
            while (open()) {
                accept().ifPresent(h -> {
                    handlers.put(h, protocol.get());
                    handlers.computeIfPresent(h, (ignored, protocol) -> protocol.advance(h, this));
                });
                for (H h : select()) {
                    try {
                        if (h.compute()) {
                            handlers.computeIfPresent(h, (ignored, protocol) -> protocol.advance(h, this));
                        } else {
                            handlers.remove(h);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("Removing client.");
                        handlers.remove(h);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stream<H> handlers() {
        return handlers.keySet().stream();
    }

    @Override
    public Set<Client> connected() {
        return handlers.entrySet()
                .stream()
                .map(entry -> new Client(entry.getKey(), entry.getValue()))
                .collect(Collectors.toUnmodifiableSet());
    }

}
