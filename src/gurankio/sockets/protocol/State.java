package gurankio.sockets.protocol;

import java.util.Optional;

@FunctionalInterface
public interface State {
    State apply(ChannelFacade channel, ServerFacade server);
}
