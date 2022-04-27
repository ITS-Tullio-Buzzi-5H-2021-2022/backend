package edu.tulliobuzzi.algoritmo;

import edu.tulliobuzzi.algoritmo.componenti.PannelloControllo;
import edu.tulliobuzzi.algoritmo.componenti.Riflettore;
import edu.tulliobuzzi.algoritmo.componenti.Rotore;

import java.util.Arrays;
import java.util.List;

public class Enigma {
    // 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25
    // A B C D E F G H I J K  L  M  N  O  P  Q  R  S  T  U  V  W  X  Y  Z
    public static final List<String> ALPHABET = List.of("ABCDEFGHIJKLMNOPQRSTUVWXYZ".split(""));

    private final Riflettore riflettore;
    private Rotore rotoreSinistro;
    private Rotore rotoreCentrale;
    private Rotore rotoreDestro;
    private final PannelloControllo pannelloControllo;

    public Enigma(Riflettore riflettore, Rotore[] rotori, PannelloControllo pannelloControllo) {
        this.riflettore = riflettore;
        this.rotoreSinistro = rotori[0];
        this.rotoreCentrale = rotori[1];
        this.rotoreDestro = rotori[2];
        this.pannelloControllo = pannelloControllo;
    }

    public void setRotore(int index, Rotore rotore) {
        switch (index) {
            case 0 -> rotoreSinistro = rotore;
            case 1 -> rotoreCentrale = rotore;
            case 2 -> rotoreDestro = rotore;
        }
    }

    public Boolean[] ruota() {
        Boolean[] ruotato = new Boolean[]{false, false, true};
        if (ruotato[1] = rotoreDestro.isAtTacca()) {
            if (ruotato[0] = rotoreCentrale.isAtTacca()) {
                rotoreSinistro.ruota();
            }
            rotoreCentrale.ruota();
        }
        rotoreDestro.ruota();
        return ruotato;
    }

    public Boolean[] ruotaIndietro() {
        Boolean[] ruotato = new Boolean[]{false, false, true};
        rotoreDestro.ruotaIndietro();
        if (ruotato[1] = rotoreDestro.isAtTacca()) {
            rotoreCentrale.ruotaIndietro();
            if (ruotato[0] = rotoreCentrale.isAtTacca()) {
                rotoreSinistro.ruotaIndietro();
            }
        }
        return ruotato;
    }

    public record Cifrazione(String cifrato, Boolean[] ruotato) {
        // Array dei booleani: {Sinistro?, Centrale?, Destro?}

        @Override
        public String toString() {
            return "Cifrazione{" +
                    "cifrato='" + cifrato + '\'' +
                    ", ruotato=" + Arrays.toString(ruotato) +
                    '}';
        }
    }

    public Cifrazione cifra(String carattere) {
        Boolean[] ruotato = this.ruota();

        // Plugboard in
        carattere = pannelloControllo.avanza(carattere);

        // Rotori da destra a sinistra
        carattere = rotoreDestro.avanza(carattere);
        carattere = rotoreCentrale.avanza(carattere);
        carattere = rotoreSinistro.avanza(carattere);

        // Riflettore
        carattere = riflettore.avanza(carattere);

        // Rotori da sinistra a destra
        carattere = rotoreSinistro.arretra(carattere);
        carattere = rotoreCentrale.arretra(carattere);
        carattere = rotoreDestro.arretra(carattere);

        // Plugboard out
        carattere = pannelloControllo.avanza(carattere);

        return new Cifrazione(carattere, ruotato);
    }

    public List<Cifrazione> cifraStringa(String input) {
        return Arrays.stream(input.split(""))
                .map(this::cifra)
                .toList();
    }

}
