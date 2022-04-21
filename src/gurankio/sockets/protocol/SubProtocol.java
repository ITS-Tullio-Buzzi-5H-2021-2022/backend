package gurankio.sockets.protocol;

import java.util.Optional;

public abstract class SubProtocol implements State {

    private final State then;

    public SubProtocol(State then) {
        this.then = then;
    }

    protected abstract State connected();

    protected State then(ChannelFacade channel, Optional<ServerFacade> server) {
        return then.apply(channel, server);
    }

    @Override
    public State apply(ChannelFacade channel, Optional<ServerFacade> server) {
        return connected().apply(channel, server);
    }
}
