package edu.tulliobuzzi.orizzontale;

import java.io.Closeable;
import java.io.IOException;

public interface Orizzontale extends Closeable {

    boolean send(String string) throws IOException;

    class NotSupportedException extends IOException {
        public NotSupportedException() {
            super("Cannot send from this side.");
        }
    }
}
