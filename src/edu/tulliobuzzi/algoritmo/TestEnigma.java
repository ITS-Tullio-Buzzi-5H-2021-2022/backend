package edu.tulliobuzzi.algoritmo;

import edu.tulliobuzzi.algoritmo.componenti.FabbricaRiflettori;
import edu.tulliobuzzi.algoritmo.componenti.FabbricaRotori;
import edu.tulliobuzzi.algoritmo.componenti.PannelloControllo;
import edu.tulliobuzzi.algoritmo.componenti.Rotore;

public class TestEnigma {

    public static void main(String[] args) {
        TestEnigma te = new TestEnigma();
        te.testAlgoritmo();
        te.testBackspace();
    }

    public void testAlgoritmo() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 100; i++)
            sb.append("A");
        String inputCharacter = sb.toString();

        Enigma enigma1 = new Enigma(
                FabbricaRiflettori.C.build(),
                new Rotore[]{
                        FabbricaRotori.I.build(0, 1),
                        FabbricaRotori.II.build(0, 2),
                        FabbricaRotori.III.build(0, 3)
                },
                new PannelloControllo("EF TI"));

        String res = enigma1.codifica(inputCharacter);
        // I E
        assert res.equals("PJRQHGTIXGPOBLYSWOMLSHZCBKLWGFQPTGJSBHCJOQZRPLSZIFBIWKGFDQLMJMXHYJXKZPQRVETSSXJUHLTNMVPWJMSORYUJNUIC");

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

        assert (res2.equals(inputCharacter));

    }

    public void testBackspace() {
        Enigma enigma1 = new Enigma(
                FabbricaRiflettori.C.build(),
                new Rotore[]{
                        FabbricaRotori.I.build(0, 1),
                        FabbricaRotori.II.build(0, 2),
                        FabbricaRotori.III.build(0, 3)
                },
                new PannelloControllo("EF TI"));

        String input = enigma1.codifica("ENIGMAISCOOLENIGMAISCOOLENIGMAISCOOLENIGMAISCOOLENIGMAISCOOLENIGMAISCOOL");
        System.out.println(input);

        String primaCodifica = enigma1.codifica("O");
        System.out.println(primaCodifica);
        enigma1.ruotaIndietro();

        String secondaCodifica = enigma1.codifica("O");
        System.out.println(secondaCodifica);

        assert(primaCodifica.equals(secondaCodifica));
    }

}
