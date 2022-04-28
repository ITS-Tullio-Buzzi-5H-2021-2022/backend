package edu.tulliobuzzi.verticale;

import java.io.Closeable;
import java.io.IOException;

public interface Verticale extends Runnable, Closeable {

    boolean send(String string) throws IOException;

    class NotSupportedException extends IOException {
        public NotSupportedException() {
            super("Cannot send from this side.");
        }
    }
}
