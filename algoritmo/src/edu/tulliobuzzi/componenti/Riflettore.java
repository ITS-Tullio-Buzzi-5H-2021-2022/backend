package edu.tulliobuzzi.componenti;

import java.util.ArrayList;

public class Riflettore extends ComponenteBase{

    public Riflettore(String configurazione) {
        super(configurazione);
    }

    @Override
    public int getLettera(int indice) {
        return this.configurazione.get(indice);
    }

    @Override
    ArrayList<Integer> convertiStringa(String codifica) {
        ArrayList<Integer> configurazione = new ArrayList<>(codifica.length());
        for (int i = 0; i < codifica.length(); i++) {
            configurazione.set(i, codifica.charAt(i) - 65);
        }
        return configurazione;
    }
}
