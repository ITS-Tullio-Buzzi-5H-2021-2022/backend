package gurankio.sockets.protocol;

/**
 * A SubProtocol may be used to share common initialization procedures.
 *
 * @author Jacopo Del Granchio
 */
public abstract class SubProtocol implements State {

    /**
     * The state of a higher level protocol to resume execution.
     */
    private final State then;

    /**
     * @param then the state from where to resume execution after the sub-protocol is done
     */
    public SubProtocol(State then) {
        this.then = then;
    }

    /**
     * This SubProtocol starting state.
     *
     * @return the starting state
     * @see Protocol
     */
    protected abstract State connected();

    /**
     * This method is used to return the execution to the higher level protocol.
     *
     * @param channel the client's channel, which allows to operate on it
     * @param server  an instance to the underlying server, which allows inter-client communication
     * @return the higher level state
     */
    protected State then(ChannelFacade channel, ServerFacade server) {
        return then.apply(channel, server);
    }

    /**
     * On first execution, forward to the specified starting state.
     *
     * @param channel the client's channel, which allows to operate on it
     * @param server  an instance to the underlying server, which allows inter-client communication
     * @return the next state
     */
    @Override
    public State apply(ChannelFacade channel, ServerFacade server) {
        return connected().apply(channel, server);
    }
}
