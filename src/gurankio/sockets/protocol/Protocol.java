package gurankio.sockets.protocol;


import java.util.Optional;

public abstract class Protocol {

    private State current;

    public Protocol() {
        current = connected();
    }

    public Protocol advance(ChannelFacade channel, ServerFacade server) {
        current = current.apply(channel, server);
        return this;
    }

    protected abstract State connected();

    public void disconnected() {
        // NO-OP
    }

}
