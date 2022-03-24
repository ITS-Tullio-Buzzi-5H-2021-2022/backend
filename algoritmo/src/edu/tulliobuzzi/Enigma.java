package edu.tulliobuzzi;

import edu.tulliobuzzi.componenti.PannelloControllo;
import edu.tulliobuzzi.componenti.Riflettore;
import edu.tulliobuzzi.componenti.Rotore;

public class Enigma {
    public static final String[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");

    // 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25
    // A B C D E F G H I J K  L  M  N  O  P  Q  R  S  T  U  V  W  X  Y  Z
    private Rotore rotoreSinistro;
    private Rotore rotoreCentrale;
    private Rotore rotoreDestro;

    public Riflettore riflettore;

    public PannelloControllo pannelloControllo;

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
        // If middle rotor notch - double-stepping
        if (rotoreCentrale.isAtTacca()) {
            rotoreCentrale.ruota();
            rotoreSinistro.ruota();
        }
        // If left-rotor notch
        else if (rotoreDestro.isAtTacca()) {
            rotoreCentrale.ruota();
        }

        // Increment right-most rotor
        rotoreDestro.ruota();
    }

    public String cifrazione(String carattere) {
        this.ruota();

        // Plugboard in
        carattere = this.pannelloControllo.avanza(carattere);

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
        if (input.length() > 1) {
            StringBuilder risultato = new StringBuilder();
            for (String carattere : input.split("")) {
                risultato.append(this.codifica(carattere));
            }
            return risultato.toString();
        }
        return this.cifrazione(input);
    }

}
