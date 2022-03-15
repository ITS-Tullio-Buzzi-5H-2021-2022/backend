package edu.tulliobuzzi.componenti;

import java.util.ArrayList;

public abstract class ComponenteAstratto implements Componente {
    protected ArrayList<Integer> configurazione;

    public ComponenteAstratto (String codifica){
        System.out.println(codifica);
        System.out.println(convertiStringa(codifica));
        this.configurazione = convertiStringa(codifica);
    }

    abstract public int avanza(int carattere);
    public int arretra(int carattere) {
        throw new UnsupportedOperationException();
    }

    /**
     * Trasforma la lista di caratteri 'codifica'
     * in lista di interi corrispondente
     * @param codifica
     * @return
     */
    ArrayList<Integer> convertiStringa(String codifica) {
        codifica = codifica.toUpperCase();
        ArrayList<Integer> configurazione = new ArrayList<>();
        for (int i = 0; i < codifica.length(); i++) {
            // codifica ASCII a numero (A=65 -> A=0)
            configurazione.add(codifica.charAt(i) - 65);
        }
        return configurazione;
    }
}
