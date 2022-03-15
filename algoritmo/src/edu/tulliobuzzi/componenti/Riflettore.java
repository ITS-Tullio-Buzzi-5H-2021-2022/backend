package edu.tulliobuzzi.componenti;

import java.util.ArrayList;
import java.util.Collections;

public class Riflettore extends ComponenteAstratto{

    public Riflettore(String configurazione) {
        super(configurazione);
    }

    @Override
    public int avanza(int carattere) {
        return this.configurazione.get(carattere);
    }

    @Override
    ArrayList<Integer> convertiStringa(String codifica) {
        ArrayList<Integer> configurazione = new ArrayList<>(Collections.nCopies(codifica.length(), null));
        for (int i = 0; i < codifica.length(); i++) {
            configurazione.set(i, codifica.charAt(i) - 65);
        }
        return configurazione;
    }
}
