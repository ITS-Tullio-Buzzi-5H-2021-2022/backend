package edu.tulliobuzzi.algoritmo;

import edu.tulliobuzzi.algoritmo.componenti.FabbricaRiflettori;
import edu.tulliobuzzi.algoritmo.componenti.FabbricaRotori;
import edu.tulliobuzzi.algoritmo.componenti.PannelloControllo;
import edu.tulliobuzzi.algoritmo.componenti.Rotore;

import java.util.List;
import java.util.stream.Collectors;

public class TestEnigma {

    public static void main(String[] args) {
        TestEnigma te = new TestEnigma();
        te.testAlgoritmo();
        te.testBackspace();
    }

    public void testAlgoritmo() {
        String inputCharacter = "A".repeat(100);

        Enigma enigma1 = new Enigma(
                FabbricaRiflettori.C.build(),
                List.of(
                        FabbricaRotori.I.build(0, 1),
                        FabbricaRotori.II.build(0, 2),
                        FabbricaRotori.III.build(0, 3)
                ),
                new PannelloControllo("EF TI"));

        List<Enigma.Cifrazione> results = enigma1.cifraStringa(inputCharacter);
        String res = results.stream().map(Enigma.Cifrazione::cifrato).collect(Collectors.joining());

        Enigma enigma2 = new Enigma(
                FabbricaRiflettori.C.build(),
                List.of(
                        FabbricaRotori.I.build(0, 1),
                        FabbricaRotori.II.build(0, 2),
                        FabbricaRotori.III.build(0, 3)
                ),
                new PannelloControllo("EF TI"));

        List<Enigma.Cifrazione> results2 = enigma2.cifraStringa(res);
        String res2 = results2.stream().map(Enigma.Cifrazione::cifrato).collect(Collectors.joining());

        System.out.println("Input: " + inputCharacter);
        System.out.println("Codifica: " + res);
        System.out.println(results);
        System.out.println("Decodifica: " + res2);
        System.out.println(results2);

        assert (res2.equals(inputCharacter));

    }

    public void testBackspace() {
        Enigma enigma1 = new Enigma(
                FabbricaRiflettori.C.build(),
                List.of(
                        FabbricaRotori.I.build(0, 1),
                        FabbricaRotori.II.build(0, 2),
                        FabbricaRotori.III.build(0, 3)
                ),
                new PannelloControllo("EF TI"));

        List<Enigma.Cifrazione> input = enigma1.cifraStringa("ENIGMAISCOOL");
        System.out.println(input);
        List<Enigma.Cifrazione> primaCodifica = enigma1.cifraStringa("O");
        System.out.println(primaCodifica);
        enigma1.ruotaIndietro();
        List<Enigma.Cifrazione> secondaCodifica = enigma1.cifraStringa("O");

        System.out.println(secondaCodifica);

        assert(primaCodifica.equals(secondaCodifica));
    }

}
