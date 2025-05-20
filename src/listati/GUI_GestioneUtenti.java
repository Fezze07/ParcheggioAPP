package listati;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.util.Objects;
import java.util.function.Consumer;

public class GUI_GestioneUtenti {
    public static GestioneUtenti gestioneUtenti = new GestioneUtenti();
    private static BorderPane base;
    public static void setBase(BorderPane root) {
        base = root;
    }

    public static void autenticazione(Consumer<Utente> callback) {
        Button pulsAccedi = InterfacciaHelper.creaPulsante("Accedi", _-> accedi(callback));
        Button pulsRegistrati = InterfacciaHelper.creaPulsante("Registrati", _ -> registrati(callback));
        Button pulsChiSiamo = InterfacciaHelper.creaPulsante("Chi siamo?", _ -> Animazioni.mostraInfoChiSiamo());
        pulsChiSiamo.getStyleClass().add("chi-siamo");

        //HBox
        HBox pulsantiBox = new HBox(40, pulsAccedi, pulsRegistrati);
        pulsantiBox.setAlignment(Pos.CENTER);

        Label titolo = new Label("Cisera Cars");
        titolo.getStyleClass().add("titolo");
        Label sottotitolo = new Label("di Federico Cisera");
        sottotitolo.getStyleClass().add("sottotitolo");
        VBox boxTitoli = new VBox(5, titolo, sottotitolo);
        boxTitoli.setAlignment(Pos.TOP_RIGHT);

        // Immagine
        ImageView logo = new ImageView(new Image(Objects.requireNonNull(GUI_GestioneUtenti.class.getResource("/risorse/immagini/logo.png")).toExternalForm()));
        Animazioni.creaLogo(logo);
        HBox topSection = new HBox(20, logo, boxTitoli);
        topSection.setAlignment(Pos.TOP_RIGHT);
        topSection.setSpacing(20);
        topSection.setPadding(new Insets(50));
        topSection.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(boxTitoli, Priority.ALWAYS);

        // Orologio
        VBox boxDataOra = InterfacciaHelper.creaBoxDataOra();

        // Testo invito
        Label invito = new Label("Pronto ad accedere ai servizi di Cisera Cars?");
        invito.getStyleClass().add("invito");

        // Crea il layout principale
        VBox contenutoCentrale  = new VBox(50, topSection, invito, pulsantiBox, pulsChiSiamo);
        contenutoCentrale.setAlignment(Pos.TOP_CENTER);

        BorderPane layout = new BorderPane();
        layout.setCenter(contenutoCentrale);
        layout.setBottom(boxDataOra);

        base.setCenter(layout);
    }

    public static void accedi(Consumer<Utente> callback) {
        // Crea i campi per inserire i dati
        Label labelNome = InterfacciaHelper.creaLabel("Nome Utente");
        TextField campoNome = InterfacciaHelper.creaCampoTesto("");
        Label labelPassword = InterfacciaHelper.creaLabel("Password");
        PasswordField campoPassword = InterfacciaHelper.creaCampoPassword("");

        // Pulsante per accedere
        Button accedi = InterfacciaHelper.creaPulsante("Accedi", _ -> {
            String nome = campoNome.getText();
            String password = campoPassword.getText();
            if (gestioneUtenti.verificaCredenziali(nome, password)) {
                Utente u = gestioneUtenti.ritornaUtente(nome);
                callback.accept(u);
            } else {
                InterfacciaHelper.mostraErrore("Impossibile trovare l'utente");
            }
        });
        Button indietro = InterfacciaHelper.creaPulsante("Non ancora registrato, fallo ora qua sotto!", _ -> registrati(callback));
        indietro.getStyleClass().setAll("indietro");
        Button esci = InterfacciaHelper.creaPulsante("Esci", _ -> autenticazione(_ -> {}));
        HBox pulsantiBox = new HBox(40, accedi, esci);
        pulsantiBox.setAlignment(Pos.CENTER);

        // Layout per la finestra di prenotazione
        VBox layout = new VBox(15,labelNome, campoNome, labelPassword, campoPassword, pulsantiBox, indietro);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        base.setCenter(layout);
    }

    public static void registrati(Consumer<Utente> callback) {
        // Crea i campi per inserire i dati
        Label labelNome = InterfacciaHelper.creaLabel("Nome Utente");
        TextField campoNome = InterfacciaHelper.creaCampoTesto("");
        Label labelPassword = InterfacciaHelper.creaLabel("Password");
        PasswordField campoPassword = InterfacciaHelper.creaCampoPassword("");

        // Pulsante per confermare la prenotazione
        Button registrati = InterfacciaHelper.creaPulsante("Registrati", _ -> {
            String nome = campoNome.getText();
            String password = campoPassword.getText();
            if (nome.isEmpty() || password.isEmpty()) {
                InterfacciaHelper.mostraErrore("Nome utente e password non possono essere vuoti!");
            } else if (gestioneUtenti.verificaDisponibilitaNome(nome)) {
                InterfacciaHelper.mostraErrore("Nome utente già in uso!");
            } else {
                Utente a = new Utente("Utente", nome, password);
                gestioneUtenti.aggiungiUtente(a);
                callback.accept(a);
            }
        });
        Button esci = InterfacciaHelper.creaPulsante("Esci", _ -> autenticazione(_ -> {}));
        // Layout per la finestra di prenotazione
        VBox layout = new VBox(10, labelNome, campoNome, labelPassword, campoPassword, registrati, esci);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        base.setCenter(layout);
    }

    public static void cambiaPassword(Utente utente) {
        Label labelPassword = InterfacciaHelper.creaLabel("Nuova password!");
        PasswordField campoPassword = InterfacciaHelper.creaCampoPassword("");

        final Button[] salva = new Button[1];
        salva[0] = InterfacciaHelper.creaPulsante("Salva Nuova Password", _ -> {
            String nuovaPassword = campoPassword.getText().trim();
            if (nuovaPassword.isEmpty()) {
                InterfacciaHelper.mostraErrore("La password non può essere vuota!");
            } else {
                GestioneUtenti.cambiaPassword(utente, nuovaPassword);
                InterfacciaHelper.mostraConfermaRunnable("Password cambiata con successo!", () -> ParcheggioApp.mostraMenuUtente(utente));
            }
        });
        Button esci = InterfacciaHelper.creaPulsante("Esci", _ -> ParcheggioApp.mostraMenuUtente(utente));
        VBox layout = new VBox(10, labelPassword, campoPassword, salva[0], esci);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(50));
        base.setCenter(layout);
    }

    public static void cambiaNomeUtente(Utente utente) {
        Label labelNome = InterfacciaHelper.creaLabel("Nuovo nome utente");
        TextField campoNome = InterfacciaHelper.creaCampoTesto("");

        final Button[] salva = new Button[1];
        salva[0] = InterfacciaHelper.creaPulsante("Salva Nuovo Nome", _ -> {
            String nuovoNome = campoNome.getText().trim();
            if (nuovoNome.isEmpty()) {
                InterfacciaHelper.mostraErrore("Il nome utente non può essere vuoto!");
            } else if (gestioneUtenti.verificaDisponibilitaNome(nuovoNome)) {
                InterfacciaHelper.mostraErrore("Nome utente già in uso!");
            } else {
                gestioneUtenti.cambiaNomeUtente(utente, nuovoNome);
                InterfacciaHelper.mostraConfermaRunnable("Nome utente cambiato con successo!", () -> ParcheggioApp.mostraMenuUtente(utente));
            }
        });
        Button esci = InterfacciaHelper.creaPulsante("Esci", _ -> ParcheggioApp.mostraMenuUtente(utente));
        VBox layout = new VBox(10, labelNome, campoNome, salva[0], esci);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(50));
        base.setCenter(layout);
    }
}