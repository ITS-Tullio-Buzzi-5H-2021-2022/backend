package edu.tulliobuzzi.algoritmo;

import edu.tulliobuzzi.algoritmo.componenti.PannelloControllo;
import edu.tulliobuzzi.algoritmo.componenti.Riflettore;
import edu.tulliobuzzi.algoritmo.componenti.Rotore;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Enigma {
    // 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25
    // A B C D E F G H I J K  L  M  N  O  P  Q  R  S  T  U  V  W  X  Y  Z
    public static final List<String> ALPHABET = List.of("ABCDEFGHIJKLMNOPQRSTUVWXYZ".split(""));

    private Riflettore riflettore;
    private List<Rotore> rotori;
    private PannelloControllo pannelloControllo;

    public Enigma(Riflettore riflettore, List<Rotore> rotori, PannelloControllo pannelloControllo) {
        this.riflettore = riflettore;
        this.rotori = rotori;
        this.pannelloControllo = pannelloControllo;
    }

    public void setRiflettore(Riflettore riflettore) {
        this.riflettore = riflettore;
    }

    public void setRotore(int index, Rotore rotore) {
        rotori.set(index, rotore);
    }

    public void setCavi(String codifica) {
        pannelloControllo = new PannelloControllo(codifica);
    }

    public Boolean[] ruota() {
        Boolean[] ruotato = Stream.generate(() -> false).limit(rotori.size()).toArray(Boolean[]::new);
        ruotato[ruotato.length - 1] = true;
        for (int i = rotori.size() - 1; i >= 0; i--) {
            if (ruotato[i]) {
                if (i != 0) ruotato[i - 1] = rotori.get(i).isAtTacca();
                rotori.get(i).ruota();
            }
        }
        return ruotato;
    }

    public Boolean[] ruotaIndietro() {
        Boolean[] ruotato = Stream.generate(() -> false).limit(rotori.size()).toArray(Boolean[]::new);
        ruotato[ruotato.length - 1] = true;
        for (int i = rotori.size() - 1; i >= 0; i--) {
            if (ruotato[i]) {
                rotori.get(i).ruotaIndietro();
                if (i != 0) ruotato[i - 1] = rotori.get(i).isAtTacca();
            }
        }
        return ruotato;
    }

    public Cifrazione cifra(String carattere) {
        Boolean[] ruotato = this.ruota();

        // Plugboard in
        carattere = pannelloControllo.avanza(carattere);

        // Rotori da destra a sinistra
        for (int i = rotori.size() - 1; i >= 0; i--) {
            carattere = rotori.get(i).avanza(carattere);
        }

        // Riflettore
        carattere = riflettore.avanza(carattere);

        // Rotori da sinistra a destra
        for (int i = 0; i < rotori.size(); i++) {
            carattere = rotori.get(i).arretra(carattere);
        }

        // Plugboard out
        carattere = pannelloControllo.avanza(carattere);

        return new Cifrazione(carattere, ruotato);
    }

    public List<Cifrazione> cifraStringa(String input) {
        return Arrays.stream(input.split(""))
                .map(this::cifra)
                .toList();
    }

    @Override
    public String toString() {
        return "Enigma{" +
                "\n\triflettore=" + riflettore +
                "\n\trotori=" + rotori +
                ",\n\tpannelloControllo=" + pannelloControllo +
                "\n}";
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
}
