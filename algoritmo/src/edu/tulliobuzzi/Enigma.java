package edu.tulliobuzzi;

import edu.tulliobuzzi.componenti.Componente;
import edu.tulliobuzzi.componenti.PannelloControllo;
import edu.tulliobuzzi.componenti.Riflettore;
import edu.tulliobuzzi.componenti.Rotore;

public class Enigma {

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

    public int cifrazione(int carattere) {
        this.ruota();

        // Plugboard in
        carattere = this.pannelloControllo.avanza(carattere);

        // Rotori da destra a sinistra
        int c1 = rotoreDestro.avanza(carattere);
        int c2 = rotoreCentrale.avanza(c1);
        int c3 = rotoreSinistro.avanza(c2);

        // Riflettore
        int c4 = riflettore.avanza(c3);

        // Rotori da sinistra a destra
        int c5 = rotoreSinistro.arretra(c4);
        int c6 = rotoreCentrale.arretra(c5);
        int c7 = rotoreDestro.arretra(c6);

        // Plugboard out
        c7 = pannelloControllo.avanza(c7);

        return c7;
    }

    public char codifica(char carattere) {
        return (char)(this.cifrazione(carattere - 65) + 65);
    }

    public String codifica(String carattere) {
        carattere = carattere.toUpperCase();
        return Character.toString(codifica(carattere.charAt(0)));
    }

    public String codificaStringa(String input) {
        StringBuilder risultato = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            risultato.append(this.codifica(input.charAt(i)));
        }
        return risultato.toString();
    }
}
