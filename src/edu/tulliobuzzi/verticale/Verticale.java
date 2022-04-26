package edu.tulliobuzzi.verticale;

import java.io.Closeable;

public interface Verticale extends Closeable {

    void send(String string) throws Exception;

}
