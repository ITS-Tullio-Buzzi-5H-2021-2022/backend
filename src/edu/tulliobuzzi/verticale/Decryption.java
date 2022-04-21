package edu.tulliobuzzi.verticale;

import gurankio.WebSocket;
import gurankio.sockets.protocol.ChannelFacade;
import gurankio.sockets.protocol.Protocol;
import gurankio.sockets.protocol.ServerFacade;
import gurankio.sockets.protocol.State;

import java.nio.ByteBuffer;
import java.util.Optional;

public class Decryption extends Protocol {

    @Override
    protected State connected() {
        return new WebSocket(this::receive);
    }

    private State receive(ChannelFacade channel, Optional<ServerFacade> server) {
        // poll for stuff to send, don't actually know how.
        // send it and that's all?
        return this::receive;
    }

}
