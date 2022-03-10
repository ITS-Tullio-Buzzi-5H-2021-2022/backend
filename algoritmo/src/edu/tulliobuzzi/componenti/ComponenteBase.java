package edu.tulliobuzzi.componenti;

import java.util.ArrayList;

public abstract class ComponenteBase implements ComponenteInterface{
    ArrayList<Integer> configurazione;

    public ComponenteBase (String codifica){
        this.configurazione = convertiStringa(codifica);
    }

    abstract public int getLettera(int indice);

    ArrayList<Integer> convertiStringa(String codifica) {
        ArrayList<Integer> configurazione = new ArrayList<>(codifica.length());
        for (int i = 0; i < codifica.length(); i++) {
            configurazione.set(i, codifica.charAt(i) - 65);
        }
        return configurazione;
    }
}
