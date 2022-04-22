package edu.tulliobuzzi;

import edu.tulliobuzzi.algoritmo.Enigma;
import edu.tulliobuzzi.algoritmo.componenti.FabbricaRiflettori;
import edu.tulliobuzzi.algoritmo.componenti.FabbricaRotori;
import edu.tulliobuzzi.algoritmo.componenti.PannelloControllo;
import edu.tulliobuzzi.algoritmo.componenti.Rotore;
import edu.tulliobuzzi.orizzontale.Orizzontale;
import edu.tulliobuzzi.verticale.Verticale;

import java.io.IOException;

public class Main {

    // probabilmente qua serviranno un paio di queue per far comunicare i componenti
    // e qui o qualcun'altro deve tenere l'istanza della macchina Enigma con la configurazione inserita.

    // Trasmissione tipo:
    // Enc:
    // {"type": "rotorFill", "rotors": ["I", "II", "III"]}
    // TODO: risposta rotorFill?
    //
    // TODO: -> {"type": "charToEncode", "data": "A" }
    // TODO: -> {"type": "encodingResult", "data":"B", "rotors": [true, false, true] }
    // {"type": "backspacePressed", "data":"Backspace"} TODO: -> {"type": "backspacePressed"}
    // {"type": "enterPressed","data":"Enter"} / TODO: -> {"type": "enterPressed"}
    // Dec:
    // TODO: invio iniziale delle informazioni sui rotori?
    //
    // {"type": "encodedText", "data": "ILSASSOFRASSO" }
    // {"type": "textToDecode", "data": "ILSASSOFRASSO", "rotors": ["I", "II", "III"]}
    // {"type": "decodedText", "data": "SASSISTAPAZZO" }

    public static Enigma enigma;
    public static Verticale verticale;
    public static Orizzontale orizzontale;

    public static void main(String[] args) throws IOException, InterruptedException {
        // TODO: factory
        switch (args[0]) {
            case "encode" -> {
                orizzontale = new edu.tulliobuzzi.orizzontale.Codifica();
                verticale = new edu.tulliobuzzi.verticale.Codifica();
            }
            case "decode" -> {
                orizzontale = new edu.tulliobuzzi.orizzontale.Decodifica();
                verticale = new edu.tulliobuzzi.verticale.Decodifica();
            }
        }
        enigma = new Enigma(FabbricaRiflettori.C.build(),
                new Rotore[]{
                        FabbricaRotori.I.build(0, 1),
                        FabbricaRotori.II.build(0, 2),
                        FabbricaRotori.III.build(0, 3)
                },
                new PannelloControllo("EF TI"));

        while (true) {
            Thread.sleep(10_000);
        }
    }
}
