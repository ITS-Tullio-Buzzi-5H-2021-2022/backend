package edu.tulliobuzzi;

import edu.tulliobuzzi.componenti.*;

public class Main {

    public static void main(String[] args) {

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 100; i++)
            sb.append("A");
        String inputCharacter = sb.toString();
        inputCharacter = "ENIGMAISCOOL";

        Enigma enigma1 = new Enigma(
                FabbricaRiflettori.C.build(),
                new Rotore[]{
                        FabbricaRotori.I.build(0, 1),
                        FabbricaRotori.II.build(0, 2),
                        FabbricaRotori.III.build(0, 3)
                },
                new PannelloControllo("EF TI"));

        String res = enigma1.codifica(inputCharacter);

        Enigma enigma2 = new Enigma(
                FabbricaRiflettori.C.build(),
                new Rotore[]{
                        FabbricaRotori.I.build(0, 1),
                        FabbricaRotori.II.build(0, 2),
                        FabbricaRotori.III.build(0, 3)
                },
                new PannelloControllo("EF TI"));

        String res2 = enigma2.codifica(res);

        System.out.println("Input: " + inputCharacter);
        System.out.println("Codifica: " + res);
        System.out.println("Decodifica: " + res2);

        assert (res2 == inputCharacter);
    }
}
