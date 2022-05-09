package gurankio.sockets.protocol;

/**
 * The base Protocol.
 *
 * This implementation interprets protocols as state machines.
 * State machines are considerably hard to express in code and are usually implemented with graph like structures
 * leading to non-clean code that is usually har to maintain.
 * In this implementation I tried for a hybrid approach, functional and object-oriented,
 * using functions as the state themselves, building an implicit graph from their subsequent call,
 * while using common OO patterns to properly express constraints in Java.
 * I've found this way easier to use, but it still needs some polishing here and there.
 *
 * @author Jacopo Del Granchio
 */
public abstract class Protocol {

    /**
     * The current State for this client.
     */
    private State current;

    public Protocol() {
        current = connected();
    }

    /**
     * Invoking this method advances the state machine.
     *
     * @param channel this client's channel
     * @param server  the underlying server
     * @return this instance
     */
    public Protocol advance(ChannelFacade channel, ServerFacade server) {
        current = current.apply(channel, server);
        return this;
    }

    /**
     * Allows for lazy initialization of the current attribute,
     * as references to methods are not valid inside constructors.
     *
     * @return the starting state.
     */
    protected abstract State connected();

    /**
     * A method that's called when the client's disconnects to allow for cleanup.
     * Defaults to a noop.
     */
    public void disconnected() {
        // NO-OP
    }

}
