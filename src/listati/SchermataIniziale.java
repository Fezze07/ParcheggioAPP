package listati;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;
import java.util.Objects;

public class SchermataIniziale {
    private static Utente utenteLoggato = null;
    private static BorderPane base;
    public static StackPane root;
    public static void setBase(BorderPane root) {
        base = root;
    }
    public static void setRoot(StackPane root2) {
        root = root2;
    }

    public static void mostraMenuIniziale() {
        ImageView logo = new ImageView(new Image(Objects.requireNonNull(GUI_GestioneUtenti.class.getResource("/risorse/immagini/logo.png")).toExternalForm()));
        Animazioni.creaLogo(logo);
        VBox boxLogo = new VBox(logo);
        boxLogo.setAlignment(Pos.CENTER_LEFT);

        Label titolo = new Label("Cisera Cars");
        titolo.getStyleClass().add("titolo");
        Label sottotitolo = new Label("La sosta in vera e totale sicurezza!");
        sottotitolo.getStyleClass().add("sottotitolo");
        sottotitolo.setPadding(new Insets(0, 0, 10, 0));
        VBox boxTitolo = new VBox(titolo, sottotitolo);
        boxTitolo.setAlignment(Pos.CENTER);

        BorderPane top = new BorderPane();
        top.setLeft(boxLogo);
        top.setCenter(boxTitolo);
        top.setPadding(new Insets(20));
        HBox.setHgrow(boxLogo, Priority.ALWAYS);
        HBox.setHgrow(boxTitolo, Priority.ALWAYS);

        VBox menuPulsanti = new VBox(15);
        menuPulsanti.setAlignment(Pos.CENTER);
        menuPulsanti.setPadding(new Insets(20));

        // Pulsanti autenticazione
        Button pulsAccedi = InterfacciaHelper.creaPulsante("Accedi", _ -> GUI_GestioneUtenti.accedi(SchermataIniziale::aggiornaMenu));
        Button pulsRegistrati = InterfacciaHelper.creaPulsante("Registrati", _ -> GUI_GestioneUtenti.registrati(SchermataIniziale::aggiornaMenu));
        Button pulsChiSiamo = InterfacciaHelper.creaPulsante("Chi siamo?", _ -> Animazioni.mostraInfoChiSiamo());
        pulsChiSiamo.getStyleClass().add("chi-siamo");
        Button pulsPrenotaPosto = creaPulsanteProtetto("Prenota un posto", "prenotazione");
        Button pulsVerificaDisponibilita = InterfacciaHelper.creaPulsante("Verifica disponibilità", _ -> GestioneParcheggio.visualizzaParcheggio(null));
        Button pulsDisdirePrenotazione = creaPulsanteProtetto("Disdici prenotazione", "disdici");
        Button pulsCambiaPassword = creaPulsanteProtetto("Cambia Password", "cambiaPassword");
        Button pulsCambiaNomeUtente = creaPulsanteProtetto("Cambia Nome Utente", "cambiaNome");
        //Button pulsLogout = InterfacciaHelper.creaPulsante("Logout", _ -> InterfacciaHelper.effettuaLogout());
        menuPulsanti.getChildren().addAll(pulsAccedi, pulsRegistrati, pulsChiSiamo, pulsPrenotaPosto, pulsVerificaDisponibilita, pulsDisdirePrenotazione, pulsCambiaPassword, pulsCambiaNomeUtente);

        VBox contenitoreMenu = new VBox(menuPulsanti);
        contenitoreMenu.setPadding(new Insets(20));
        contenitoreMenu.setAlignment(Pos.TOP_CENTER);
        contenitoreMenu.setMinWidth(300);

        VBox boxParcheggioLive = new VBox(10);
        boxParcheggioLive.setAlignment(Pos.CENTER);
        boxParcheggioLive.setMaxSize(250, 250);
        boxParcheggioLive.setPadding(new Insets(10));

        VBox boxOrologio = InterfacciaHelper.creaBoxDataOra();
        boxOrologio.setAlignment(Pos.CENTER);
        StackPane boxParcheggio = new StackPane();
        boxParcheggio.setMaxSize(250, 250);
        Timeline timeline = new Timeline(
                new KeyFrame(javafx.util.Duration.seconds(0), _ -> boxParcheggio.getChildren().setAll(GUI_VisualizzaParcheggio.creaBoxParcheggioLive())),
                new KeyFrame(Duration.seconds(10))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        boxParcheggioLive.getChildren().addAll(boxOrologio, boxParcheggio);

        HBox contenitorePrincipale = new HBox(50, contenitoreMenu, boxParcheggioLive);
        contenitorePrincipale.setAlignment(Pos.TOP_CENTER);
        contenitorePrincipale.setPadding(new Insets(20));

        BorderPane layout = new BorderPane();
        layout.setTop(top);
        layout.setCenter(contenitorePrincipale);
        layout.setPadding(new Insets(10));
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
        //Button pulsLogout = InterfacciaHelper.creaPulsante("Logout", _ -> InterfacciaHelper.effettuaLogout());
        VBox layout = creaLayoutMenu(pulsSimulaPrenotazione, pulsCancellaUtenti, pulsEsportaDatabase, pulsCaricaDatabaseUtenti, pulsVisualizzaPrenotazioniAttive, pulsModificaOrari, pulsGestioneTariffe, pulsChiudiGiorni /*pulsLogout*/);
        base.setCenter(layout);
    }

    public static void aggiornaMenu(Utente utente) {
        utenteLoggato = utente;
        if (utente != null && "Admin".equals(utente.getTipo())) {
            mostraMenuAdmin(utente);
        } else {
            mostraMenuIniziale();
        }
    }

    private static Button creaPulsanteProtetto(String testo, String azione) {
        return InterfacciaHelper.creaPulsante(testo, _ -> {
            if (utenteLoggato == null) {
                InterfacciaHelper.mostraErrore("Devi essere loggato per usare questa funzione !");
            } else {
                switch (azione) {
                    case "prenotazione" -> GUI_Prenotazione.mostraFinestraPrenotazione(utenteLoggato, null);
                    case "disdici" -> GestioneParcheggio.cancella(utenteLoggato);
                    case "cambiaPassword" -> GUI_GestioneUtenti.cambiaPassword(utenteLoggato);
                    case "cambiaNome" -> GUI_GestioneUtenti.cambiaNomeUtente(utenteLoggato);
                }
            }
        });
    }

    private static VBox creaLayoutMenu(Button... pulsanti) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(50));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(pulsanti);
        return layout;
    }
}
