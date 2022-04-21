package gurankio.sockets;

import gurankio.sockets.protocol.Protocol;
import gurankio.sockets.protocol.ServerFacade;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractServer<H extends AbstractHandler> implements ServerFacade, Closeable, Runnable {

    private final Supplier<Protocol> protocol;
    private final Map<H, Protocol> handlers;

    public AbstractServer(Supplier<Protocol> protocol) {
        this.protocol = protocol;
        this.handlers = new HashMap<>(); // TODO: Concurrent?
    }

    protected abstract boolean open();

    protected abstract Optional<H> accept() throws IOException;

    protected abstract Stream<H> select() throws IOException;

    @Override
    public void run() {
        try (this) {
            while (open()) {
                accept().ifPresent(h -> {
                    handlers.put(h, protocol.get());
                    handlers.computeIfPresent(h, (ignored, protocol) -> protocol.advance(h, Optional.of(this)));
                });
                select().forEach(h -> {
                    try {
                        if (h.compute()) {
                            handlers.computeIfPresent(h, (ignored, protocol) -> protocol.advance(h, Optional.of(this)));
                        } else {
                            handlers.remove(h);
                        }
                    } catch (IOException e) {
                        e.printStackTrace(); // TODO: ???
                    }
                });
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
