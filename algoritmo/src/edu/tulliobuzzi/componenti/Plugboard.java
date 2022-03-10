package edu.tulliobuzzi.componenti;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Plugboard extends ComponenteBase{

    public Plugboard(String configurazione) {
        super(configurazione);
    }

    @Override
    public int getLettera(int indice) {
        return this.configurazione.get(indice);
    }

    private ArrayList<Integer> inizializzaPlugboard() {
        ArrayList<Integer> configurazione = new ArrayList<>(26);
        for (int i = 0; i < 26; i++) {
            configurazione.set(i,i);
        }
        return configurazione;
    }

    ArrayList<Integer> convertiStringa(String codifica) {

        ArrayList<Integer> configurazione = inizializzaPlugboard();

        if (codifica == null || codifica.equals("")) {
            return configurazione;
        }

        String[] coppie = codifica.split("[^a-zA-Z]");
        Set<Integer> caratteriCollegati = new HashSet<>();

        //validazione configurazione
        for (String coppia : coppie) {
            if (coppia.length() != 2)
                return configurazione;

            int c1 = coppia.charAt(0) - 65;
            int c2 = coppia.charAt(1) - 65;

            if (caratteriCollegati.contains(c1) || caratteriCollegati.contains(c2)) {
                return configurazione;
            }

            caratteriCollegati.add(c1);
            caratteriCollegati.add(c2);

            configurazione.set(c1,c2);
            configurazione.set(c2,c1);
        }

        return configurazione;
    }
}
