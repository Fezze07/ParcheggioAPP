package listati;

import javafx.scene.control.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class GestionePrenotazioni {
    public static void prenota(Utente utente, String nome, String cognome, String targa, String tipo, DatePicker dataPicker, Slider orarioComboArrivo, Slider orarioComboPartenza, double costo, int x, int y) {
        LocalDateTime dataOrarioArrivo = InterfacciaHelper.creaDataOrario(dataPicker.getValue(), orarioComboArrivo);
        LocalDateTime dataOrarioPartenza = InterfacciaHelper.creaDataOrario(dataPicker.getValue(), orarioComboPartenza);
        if (utente.getTipo().equalsIgnoreCase("Utente")) {
            Posto nuovo = new Posto(utente, nome, cognome, targa, tipo, costo, dataOrarioArrivo, dataOrarioPartenza, x, y);
            GestioneParcheggio.prenota(nuovo);
        }
    }

    public static double calcolaCosto(String tipo, LocalDate data, Slider orarioArrivo, Slider orarioPartenza, boolean usaExtra, CheckBox[] opzioniExtra) {
        LocalDateTime arrivo = null;
        LocalDateTime partenza = null;
        if (data != null && orarioArrivo != null && orarioPartenza != null) {
            try {
                arrivo = InterfacciaHelper.creaDataOrario(data, orarioArrivo);
                partenza = InterfacciaHelper.creaDataOrario(data, orarioPartenza);
            } catch (Exception _) {
            }
        }
        return Prezzi.calcolaTotale(tipo, data, arrivo, partenza, usaExtra, opzioniExtra);
    }
}
