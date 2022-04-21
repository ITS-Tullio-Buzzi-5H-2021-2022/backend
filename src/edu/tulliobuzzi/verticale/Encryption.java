package edu.tulliobuzzi.verticale;

import gurankio.WebSocket;
import gurankio.sockets.protocol.ChannelFacade;
import gurankio.sockets.protocol.Protocol;
import gurankio.sockets.protocol.ServerFacade;
import gurankio.sockets.protocol.State;

import java.nio.ByteBuffer;
import java.util.Optional;

public class Encryption extends Protocol {

    @Override
    protected State connected() {
        return new WebSocket(this::receive);
    }

    private State receive(ChannelFacade channel, Optional<ServerFacade> server) {
        Optional<ByteBuffer> buffer = channel.poll();
        if (buffer.isEmpty()) {
            channel.read();
            return this::receive;
        }
        String message = WebSocket.decode(buffer.get());
        // JSON decode
        int type = 0;
        // ...
        switch (type) {
            case 0: // 'key pressed'
                // send to Enigma instance and buffer
                // reply to frontend with encoded char
                break;
            case 1: // 'enter'
                // tell buffer to send message to the other machine
                break;
        }

        return this::receive;
    }

}
