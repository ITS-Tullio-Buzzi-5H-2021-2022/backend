package edu.tulliobuzzi.verticale;

import java.io.Closeable;
import java.io.IOException;

/**
 * Interfaccia per la comunicazione verticale.
 */
public interface Verticale extends Runnable, Closeable {

    boolean send(String string) throws IOException;

    /**
     * Eccezione lanciata in caso di operazione non supportata.
     */
    class NotSupportedException extends IOException {
        public NotSupportedException() {
            super("Cannot send from this side.");
        }
    }
}
