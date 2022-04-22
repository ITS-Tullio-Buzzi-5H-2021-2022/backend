package edu.tulliobuzzi.verticale.pacchetti;

public class CharToEncode extends Pacchetto {

    private String data;

    public CharToEncode() {
    }

    public CharToEncode(String type, String data) {
        super(type);
        this.data = data;
    }
}
