package edu.tulliobuzzi;

import edu.tulliobuzzi.componenti.*;

public class Main {

    public static void main(String[] args) {

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 100; i++)
            sb.append("A");
        String inputCharacter = sb.toString();

        Enigma enigma1 = new Enigma(
                FabbricaRiflettori.crea("C"),
                new Rotore[]{
                        FabbricaRotori.crea("I", 0, 1),
                        FabbricaRotori.crea("II", 0, 2),
                        FabbricaRotori.crea("III", 0, 3)
                },
                new PannelloControllo("EF TI"));

        String res = enigma1.codificaStringa(inputCharacter);

        Enigma enigma2 = new Enigma(
                FabbricaRiflettori.crea("C"),
                new Rotore[]{
                        FabbricaRotori.crea("I", 0, 1),
                        FabbricaRotori.crea("II", 0, 2),
                        FabbricaRotori.crea("III", 0, 3)
                },
                new PannelloControllo("EF TI"));

        String res2 = enigma2.codificaStringa(res);

        System.out.println("Input: " + inputCharacter);
        System.out.println("Codifica: " + res);
        System.out.println("Decodifica: " + res2);

        assert (res2 == inputCharacter);
    }
}
