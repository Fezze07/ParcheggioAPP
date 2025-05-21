package listati;

import java.util.ArrayList;
import java.util.List;

public class GestioneParcheggio {
    private final static List<Posto> postiPrenotati = new ArrayList<>();

    public static void prenota(Posto posto) {
        postiPrenotati.add(posto);
        esportaParcheggio();
    }

    public static void cancella(Utente utente) {
        FunzioniAdmin.eliminaPrenotazione(postiPrenotati, utente);
    }

    public static void visualizzaParcheggio(Utente utente) {
        GUI_VisualizzaParcheggio.mostraParcheggioScegliOra(utente);
    }

    public static List<Posto> getPostiPrenotati() {
        return postiPrenotati;
    }

    public static void esportaParcheggio() {
        SalvaCarica.esportaParcheggio(postiPrenotati);
    }

    public static void caricaParcheggio() {
        List<Posto> caricati = SalvaCarica.caricaParcheggio(null);
        postiPrenotati.clear();
        postiPrenotati.addAll(caricati);
    }

    public static void caricaOrari() {
        SalvaCarica.caricaOrari();
    }

    public static void caricaPrezzi() {
        SalvaCarica.caricaPrezzi();
    }

    public static void visualizzaPrenotazioniAttive(Utente utente) {
        FunzioniAdmin.visualizzaPrenotazioniAttive(postiPrenotati, utente);
    }
}