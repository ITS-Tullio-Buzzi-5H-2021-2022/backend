package edu.tulliobuzzi.algoritmo;

import edu.tulliobuzzi.algoritmo.componenti.PannelloControllo;
import edu.tulliobuzzi.algoritmo.componenti.Riflettore;
import edu.tulliobuzzi.algoritmo.componenti.Rotore;

public class Enigma {
    // 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25
    // A B C D E F G H I J K  L  M  N  O  P  Q  R  S  T  U  V  W  X  Y  Z
    public static final String[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");

    private final Riflettore riflettore;
    private final Rotore rotoreSinistro;
    private final Rotore rotoreCentrale;
    private final Rotore rotoreDestro;
    private final PannelloControllo pannelloControllo;

    public Enigma(Riflettore riflettore, Rotore[] rotori, PannelloControllo pannelloControllo) {
        this.riflettore = riflettore;
        this.rotoreSinistro = rotori[0];
        this.rotoreCentrale = rotori[1];
        this.rotoreDestro = rotori[2];
        this.pannelloControllo = pannelloControllo;
    }

    public void aggiornaStato() {

    }

    public void ruota() {
        if(rotoreDestro.isAtTacca()) {
            if(rotoreCentrale.isAtTacca()) {
                rotoreSinistro.ruota();
            }
            rotoreCentrale.ruota();
        }
        rotoreDestro.ruota();
    }

    public void ruotaIndietro() {
        rotoreDestro.ruotaIndietro();

        if(rotoreDestro.isAtTacca()) {
            rotoreCentrale.ruotaIndietro();
        }

        if(rotoreCentrale.isAtTacca()) {
            rotoreCentrale.ruotaIndietro();
            rotoreSinistro.ruotaIndietro();
        }
    }

    public String cifrazione(String carattere) {
        this.ruota();

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

        return carattere;
    }

    public String codifica(String input) {

        StringBuilder risultato = new StringBuilder();
        for (String carattere : input.split("")) {
            risultato.append(this.cifrazione(carattere));
        }
        return risultato.toString();
    }

}
