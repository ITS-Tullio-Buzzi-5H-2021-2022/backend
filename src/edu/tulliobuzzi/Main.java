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
    // risposta rotorFill?
    //
    // -> {"type": "charToEncode", "data": "A" }
    // -> {"type": "encodingResult", "data":"B", "rotors": [true, false, true] }
    // {"type": "backspacePressed", "data":"Backspace"} {"type": "backspacePressed"}
    // {"type": "enterPressed","data":"Enter"} / -> {"type": "enterPressed"}
    // Dec:
    // invio iniziale delle informazioni sui rotori?
    //
    // {"type": "encodedText", "data": "ILSASSOFRASSO" }
    // {"type": "textToDecode", "data": "ILSASSOFRASSO", "rotors": ["I", "II", "III"]}
    // {"type": "decodedText", "data": "SASSISTAPAZZO" }

    public static Verticale VERTICALE;
    public static Orizzontale ORIZZONTALE;

    public static void main(String[] args) throws IOException, InterruptedException {
        // TODO: factory?
        switch (args[0]) {
            case "encode" -> {
                ORIZZONTALE = new edu.tulliobuzzi.orizzontale.Codifica();
                VERTICALE = new edu.tulliobuzzi.verticale.Codifica();
            }
            case "decode" -> {
                ORIZZONTALE = new edu.tulliobuzzi.orizzontale.Decodifica();
                VERTICALE = new edu.tulliobuzzi.verticale.Decodifica();
            }
        }

        VERTICALE.run();
    }
}
