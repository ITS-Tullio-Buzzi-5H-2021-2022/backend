package edu.tulliobuzzi;

import edu.tulliobuzzi.componenti.*;

public class Main {

    public static void main(String[] args) {

        String inputCharacter = "T";

        Enigma enigma1 = new Enigma(
                FabbricaRiflettori.crea("C"),
                new Rotore[]{
                        FabbricaRotori.crea("VII", 0, 0),
                        FabbricaRotori.crea("I", 0, 0),
                        FabbricaRotori.crea("II", 0, 0)
                },
                new PannelloControllo(""));

        String res = enigma1.codifica(inputCharacter);

        Enigma enigma2 = new Enigma(
                FabbricaRiflettori.crea("C"),
                new Rotore[]{
                        FabbricaRotori.crea("VII", 0, 0),
                        FabbricaRotori.crea("I", 0, 0),
                        FabbricaRotori.crea("II", 0, 0)
                },
                new PannelloControllo(""));

        String res2 = enigma2.codifica(res);
        System.out.println("Codifica: " + res);
        System.out.println("Decodifica: " + res2);
        System.out.println("Input: " + inputCharacter);

        assert (res2 == inputCharacter);
    }
}
