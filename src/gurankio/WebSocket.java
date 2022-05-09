package gurankio;

import gurankio.sockets.protocol.ChannelFacade;
import gurankio.sockets.protocol.ServerFacade;
import gurankio.sockets.protocol.State;
import gurankio.sockets.protocol.SubProtocol;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A partial implementation of WebSockets as per RFC6455.
 *
 * @author Jacopo Del Granchio
 */
public class WebSocket extends SubProtocol {

    private static final Pattern websocketKey = Pattern.compile("Sec-WebSocket-Key: (.+)");
    private static final Base64.Encoder base64 = Base64.getEncoder();
    private static MessageDigest sha1;

    // This thing smells bad.
    static {
        try {
            sha1 = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private StringBuilder request;

    public WebSocket(State then) {
        super(then);
        request = new StringBuilder();
    }

    /**
     * Decodes a WebSocket packet in a string.
     *
     * @param encoded the received buffer
     * @return the decoded contents a string
     */
    public static String decode(ByteBuffer encoded) throws ShortPacketException {
        // System.out.println(Arrays.toString(encoded.array()));
        byte first = encoded.get();
        boolean fin = (first & 0b10000000) != 0;
        int opcode = first & 0b00001111;
        // System.out.println(fin);
        // System.out.println(opcode);

        byte second = encoded.get();
        second &= 0b01111111;
        int lenght = -1;
        if (second <= 125) {
            lenght = second;
        }
        if (second == 126) {
            lenght = encoded.getShort();
        }
        if (second == 127) {
            lenght = (int) encoded.getLong();
        }
        // System.out.println(lenght);

        // System.out.println(encoded);
        byte[] key = new byte[4];
        encoded.get(key, 0, 4);
        // System.out.println(Arrays.toString(key));
        // System.out.println(encoded);

        ByteBuffer decoded = ByteBuffer.allocate(lenght);
        try {
            for (int i = 0; i < lenght; i++) decoded.put((byte) (encoded.get() ^ key[i & 0x3]));
        } catch (BufferUnderflowException e) {
            throw new ShortPacketException(lenght);
        }

        return StandardCharsets.UTF_8.decode(decoded.flip()).toString();
    }

    /**
     * Encodes a string as a valid WebSocket packet.
     *
     * @param message the message to encode
     * @return the encoded packet bytes
     */
    public static ByteBuffer encode(String message) {
        ByteBuffer payload = StandardCharsets.UTF_8.encode(message);
        ByteBuffer encoded = ByteBuffer.allocate(16 + payload.limit());
        encoded.put((byte) 0b10000001); // FIN=1, COMPRESSION=0, _, _, OPCODE=1
        int length = payload.limit();
        if (length <= 125) {
            encoded.put((byte) (length));
        } else if (length <= 65536) {
            encoded.put((byte) 126);
            encoded.putShort((short) length);
        } else {
            encoded.put((byte) 127);
            encoded.putLong(length);
        }

        encoded.put(payload);
        encoded.flip();
        // System.out.println(encoded);
        // System.out.println(Arrays.toString(encoded.array()));
        return encoded;
    }

    @Override
    protected State connected() {
        return this::handshake;
    }

    /**
     * Handles the initial handshake and returns control to the higher level protocol.
     */
    private State handshake(ChannelFacade channel, ServerFacade server) {
        Optional<ByteBuffer> buffer = channel.poll();
        if (buffer.isEmpty()) {
            channel.read();
            return this::handshake;
        }

        request.append(StandardCharsets.UTF_8.decode(buffer.get()));
        if (!request.toString().endsWith("\n")) {
            channel.read();
            return this::handshake;
        }
        String decoded = request.toString();
        request = new StringBuilder();

        Optional<String> optionalKey = decoded.lines()
                .map(String::strip)
                .map(websocketKey::matcher)
                .filter(Matcher::matches)
                .map(matcher -> matcher.group(1))
                .map(s -> s + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
                .map(s -> s.getBytes(StandardCharsets.UTF_8))
                .map(sha1::digest)
                .map(base64::encodeToString)
                .findAny();

        if (optionalKey.isEmpty()) {
            channel.read();
            return this::handshake;
        }

        String response = """
                HTTP/1.1 101 Switching Protocols\r
                Connection: Upgrade\r
                Upgrade: websocket\r
                Sec-WebSocket-Accept: %s\r
                \r
                """.formatted(optionalKey.get());

        channel.write(StandardCharsets.UTF_8.encode(response));
        channel.read();
        return this::then;
    }

    /**
     * Exception that's raised if the decoded packet buffer is too little.
     * Knows how long it should be.
     */
    public static class ShortPacketException extends Exception {

        private final long expectedLength;

        public ShortPacketException(long expectedLength) {
            this.expectedLength = expectedLength;
        }

        public long getExpectedLength() {
            return expectedLength;
        }
    }

}