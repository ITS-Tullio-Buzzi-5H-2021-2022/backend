package edu.tulliobuzzi.orizzontale;

import java.io.Closeable;

public interface Orizzontale extends Closeable {

    void send(String string) throws Exception;

}
