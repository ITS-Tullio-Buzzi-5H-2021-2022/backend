package gurankio.sockets.protocol;


import java.util.Optional;

public abstract class Protocol {

    private State current;

    public Protocol() {
        current = connected();
    }

    public Protocol advance(ChannelFacade channel, Optional<ServerFacade> server) {
        current = current.apply(channel, server);
        return this;
    }

    public Protocol advance(ChannelFacade channel, ServerFacade server) {
        return advance(channel, Optional.ofNullable(server));
    }

    protected abstract State connected();

    public void disconnected() {
        // NO-OP
    }

}
