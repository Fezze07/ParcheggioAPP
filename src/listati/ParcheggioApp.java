package listati;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.Objects;

public class ParcheggioApp extends Application {
    static BorderPane base;
    StackPane root;

    @Override
    public void start(Stage primaryStage) {
        base = new BorderPane();
        root = new StackPane();
        root.getChildren().add(base);
        Scene scene = new Scene(root);
        applicaStile(scene);
        InterfacciaHelper.creaStage(primaryStage, scene);
        inizializzaRootPane(base, root);

        GestioneParcheggio.caricaParcheggio();
        GestioneUtenti.caricaDatabaseUtenti();
        GestioneParcheggio.caricaPrezzi();
        InterfacciaHelper.autenticazione();
        GestioneParcheggio.caricaOrari();
    }

    public static void mostraMenuUtente(Utente utente) {
        Button pulsPrenotaPosto = InterfacciaHelper.creaPulsante("Prenota un posto", _ -> GUI_Prenotazione.mostraFinestraPrenotazione(utente, null));
        Button pulsVerificaDisponibilita = InterfacciaHelper.creaPulsante("Verifica la disponibilità", _ -> GestioneParcheggio.visualizzaParcheggio(utente));
        Button pulsDisdirePrenotazione = InterfacciaHelper.creaPulsante("Disdici una prenotazione", _ -> GestioneParcheggio.cancella(utente));
        Button pulsCambiaPassword = InterfacciaHelper.creaPulsante("Cambia Password", _ -> GUI_GestioneUtenti.cambiaPassword(utente));
        Button pulsCambiaNomeUtente = InterfacciaHelper.creaPulsante("Cambia Nome Utente", _ -> GUI_GestioneUtenti.cambiaNomeUtente(utente));
        Button pulsLogout = InterfacciaHelper.creaPulsante("Logout", _ -> InterfacciaHelper.effettuaLogout());

        VBox layout = creaLayoutMenu(pulsPrenotaPosto, pulsVerificaDisponibilita, pulsDisdirePrenotazione, pulsCambiaPassword, pulsCambiaNomeUtente, pulsLogout);
        base.setCenter(layout);
    }

    public static void mostraMenuAdmin(Utente utente) {
        Button pulsSimulaPrenotazione = InterfacciaHelper.creaPulsante("Simula prenotazione", _ -> GUI_Prenotazione.mostraFinestraPrenotazione(utente, null));
        Button pulsCancellaUtenti = InterfacciaHelper.creaPulsante("Cancella utente", _ -> GestioneUtenti.cancellaUtenteAdmin(utente));
        Button pulsEsportaDatabase = InterfacciaHelper.creaPulsante("Esporta database parcheggio", _ -> GestioneParcheggio.esportaParcheggio());
        Button pulsCaricaDatabaseUtenti = InterfacciaHelper.creaPulsante("Carica database utenti", _ -> GestioneUtenti.caricaDatabaseUtenti());
        Button pulsVisualizzaPrenotazioniAttive = InterfacciaHelper.creaPulsante("Visualizza prenotazioni attive", _ -> GestioneParcheggio.visualizzaPrenotazioniAttive(utente));
        Button pulsModificaOrari = InterfacciaHelper.creaPulsante("Modifica orari", _ -> FunzioniAdmin.modificaApertura(utente));
        Button pulsGestioneTariffe = InterfacciaHelper.creaPulsante("Gestione tariffe", _ -> FunzioniAdmin.modificaTariffe(utente));
        Button pulsChiudiGiorni = InterfacciaHelper.creaPulsante("Chiudi Giorni", _ -> FunzioniAdmin.inserisciDateChiusura(utente));
        Button pulsLogout = InterfacciaHelper.creaPulsante("Logout", _ -> InterfacciaHelper.effettuaLogout());
        VBox layout = creaLayoutMenu(pulsSimulaPrenotazione, pulsCancellaUtenti, pulsEsportaDatabase, pulsCaricaDatabaseUtenti, pulsVisualizzaPrenotazioniAttive, pulsModificaOrari, pulsGestioneTariffe, pulsChiudiGiorni, pulsLogout);
        base.setCenter(layout);
    }

    private static VBox creaLayoutMenu(Button... pulsanti) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(50));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(pulsanti);
        return layout;
    }

    private void applicaStile(Scene scene) {
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/risorse/css/interfacciaHelper.css")).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/risorse/css/menuIniziale.css")).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/risorse/css/autenticazione.css")).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/risorse/css/prenotazione.css")).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/risorse/css/listino.css")).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/risorse/css/confermaPrenotazione.css")).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/risorse/css/visualizzaParcheggio.css")).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/risorse/css/scegliOraParcheggio.css")).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/risorse/css/modificaTariffe.css")).toExternalForm());
    }

    public static void inizializzaRootPane(BorderPane base, StackPane root) {
        InterfacciaHelper.setBase(base);
        GUI_Prenotazione.setBase(base);
        GUI_VisualizzaParcheggio.setBase(base);
        GUI_GestioneUtenti.setBase(base);
        ModificaPrenotazione.setBase(base);
        Animazioni.setBase(base);
        FunzioniAdmin.setBase(base);
        GUI_Prenotazione.setRoot(root);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
