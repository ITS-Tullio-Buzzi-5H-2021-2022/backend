package edu.tulliobuzzi.componenti;

public interface Componente {
    String avanza(String carattere);

    default String arretra(String carattere) {
        throw new UnsupportedOperationException();
    }
}
