package gurankio.sockets.protocol;

/**
 * The state of a client.
 * This functional interface is what allows the syntax of protocols to remain slick and straightforward to read.
 *
 * @author Jacopo Del Granchio
 */
@FunctionalInterface
public interface State {

    /**
     * The state of a client is stored as a reference to a function.
     *
     * @param channel the client's channel, which allows to operate on it
     * @param server  an instance to the underlying server, which allows inter-client communication
     * @return the next state
     */
    State apply(ChannelFacade channel, ServerFacade server);
}
