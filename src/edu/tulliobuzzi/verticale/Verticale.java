package edu.tulliobuzzi.verticale;

import java.io.Closeable;

public interface Verticale extends Runnable, Closeable {

    void send(String string) throws Exception;

}
