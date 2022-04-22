package edu.tulliobuzzi.verticale.pacchetti;


import java.io.Serializable;
import java.util.function.Supplier;

public class Pacchetto implements Serializable {

    private String type;

    public Pacchetto() {
    }

    public Pacchetto(String type) {
        this.type = type;
    }

    private enum Pacchetti {
        CHARTOENCODE(CharToEncode.class);

        private Class<? extends Pacchetto> target;

        Pacchetti(Class<? extends Pacchetto> target) {
            this.target = target;
        }

        public static Pacchetto fromJson() {
            return null;
        }
    }
}
