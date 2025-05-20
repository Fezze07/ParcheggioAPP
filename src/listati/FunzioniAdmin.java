package listati;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class FunzioniAdmin {
    private static BorderPane base;
    public static void setBase(BorderPane root) {
        base = root;
    }

    public static void visualizzaPrenotazioniAttive(List<Posto> postiPrenotati, Utente utente) {
        if (postiPrenotati.isEmpty()) {
            InterfacciaHelper.mostraErrore("Non ci sono prenotazioni attive!");
            return;
        }
        // Visualizzare le prenotazioni
        ComboBox<Posto> prenotazioni = new ComboBox<>();
        prenotazioni.getItems().addAll(postiPrenotati);
        prenotazioni.setPromptText("Prenotazioni Attive");
        prenotazioni.getStyleClass().add("combo-selezione");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        prenotazioni.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Posto p, boolean vuoto) {
                super.updateItem(p, vuoto);
                if (vuoto || p == null) {
                    setText(null);
                } else {
                    setText("📅 " + p.dataArrivo().format(formatter) + " - " + p.dataPartenza().format(formatter) +
                            " | 🚗 " + p.targa() +
                            " | Tipo: " + p.tipo() +
                            " | 💸 €" + p.costo());
                }
            }
        });
        prenotazioni.setButtonCell(prenotazioni.getCellFactory().call(null));
        Button esci = InterfacciaHelper.creaPulsante("Esci", _ -> ParcheggioApp.mostraMenuAdmin(utente));
        Button cancella = InterfacciaHelper.creaPulsante("Cancella prenotazione", _ -> eliminaPrenotazioneAdmin(postiPrenotati, utente));

        VBox layout = new VBox(10, prenotazioni, cancella, esci);
        layout.getStyleClass().add("box-cancellazione");
        layout.setAlignment(Pos.CENTER);
        base.setCenter(layout);
    }

    public static void eliminaPrenotazione(List<Posto> postiPrenotati, Utente utente) {
        List<Posto> prenotazioniUtente = postiPrenotati.stream().filter(p -> p.utente().equals(utente)).toList();
        if (prenotazioniUtente.isEmpty()) {
            InterfacciaHelper.mostraErrore("Non hai prenotazioni da cancellare.");
            return;
        }
        // ComboBox per selezionare quale cancellare
        ComboBox<Posto> scelta = new ComboBox<>();
        scelta.getItems().addAll(prenotazioniUtente);
        scelta.setPromptText("Seleziona una prenotazione da cancellare");
        scelta.getStyleClass().add("combo-selezione");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        scelta.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Posto p, boolean vuoto) {
                super.updateItem(p, vuoto);
                if (vuoto || p == null) {
                    setText(null);
                } else {
                    setText("Posto " + p.x() + "," + p.y() +
                            " | 📅 " + p.dataArrivo().format(formatter) + " - " + p.dataPartenza().format(formatter) +
                            " | 🚗 " + p.targa() +
                            " | Tipo: " + p.tipo() +
                            " | 💸 €" + p.costo());
                }
            }
        });
        //  Anche per la visualizzazione selezionata
        scelta.setButtonCell(scelta.getCellFactory().call(null));
        Button conferma = InterfacciaHelper.creaPulsante("Conferma cancellazione", _ -> {
            Posto selezionato = scelta.getValue();
            if (selezionato != null) {
                postiPrenotati.remove(selezionato);
                InterfacciaHelper.mostraConferma("Prenotazione eliminata con successo.");
            } else {
                InterfacciaHelper.mostraErrore("Seleziona una prenotazione prima di confermare.");
            }
        });
        Button esci = InterfacciaHelper.creaPulsante("Esci", _ -> ParcheggioApp.mostraMenuUtente(utente));
        HBox pulsantiBox = new HBox(40, conferma, esci);
        pulsantiBox.setAlignment(Pos.CENTER);
        VBox layout = new VBox(10, scelta, pulsantiBox);
        layout.getStyleClass().add("box-cancellazione");
        layout.setAlignment(Pos.CENTER);
        base.setCenter(layout);
    }

    public static void eliminaPrenotazioneAdmin(List<Posto> postiPrenotati, Utente utente) {
        ComboBox<Posto> scelta = new ComboBox<>();
        scelta.getItems().addAll(postiPrenotati);
        scelta.setPromptText("Seleziona una prenotazione da eliminare");
        scelta.getStyleClass().add("combo-selezione");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        scelta.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Posto p, boolean vuoto) {
                super.updateItem(p, vuoto);
                if (vuoto || p == null) {
                    setText(null);
                } else {
                    setText("Utente: " + p.utente()+
                            " | Posto " + p.x() + "," + p.y() +
                            "📅 " + p.dataArrivo().format(formatter) + " - " + p.dataPartenza().format(formatter) +
                            " | 🚗 " + p.targa() +
                            " | Tipo: " + p.tipo() +
                            " | 💸 €" + p.costo());
                }
            }
        });
        scelta.setButtonCell(scelta.getCellFactory().call(null));
        Button esci = InterfacciaHelper.creaPulsante("Esci", _ -> ParcheggioApp.mostraMenuAdmin(utente));
        Button conferma = InterfacciaHelper.creaPulsante("Conferma Eliminazione", _ -> {
            Posto selezionato = scelta.getValue();
            if (selezionato != null) {
                postiPrenotati.remove(selezionato);
                InterfacciaHelper.mostraConferma("Prenotazione eliminata.");
            } else {
                InterfacciaHelper.mostraErrore("Seleziona una prenotazione prima.");
            }
        });

        VBox layout = new VBox(10, scelta, conferma, esci);
        layout.getStyleClass().add("box-cancellazione");
        layout.setAlignment(Pos.CENTER);
        base.setCenter(layout);
    }

    public static void cancellaUtente(List<Utente> databaseUtenti, Utente utente) {
        ComboBox<Utente> scelta = new ComboBox<>();
        scelta.setPromptText("Seleziona un utente da eliminare");
        scelta.getStyleClass().add("combo-selezione");
        // Filtra e mostra solo gli utenti non admin
        for (Utente u : databaseUtenti) {
            if (!"Admin".equalsIgnoreCase(u.tipo)) {
                scelta.getItems().add(u);
            }
        }
        scelta.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Utente u, boolean vuoto) {
                super.updateItem(u, vuoto);
                if (vuoto || u == null) {
                    setText(null);
                } else {
                    setText("👤 " + u.nomeUtente + " | 🔒 " + u.password);
                }
            }
        });
        scelta.setButtonCell(scelta.getCellFactory().call(null));
        Button esci = InterfacciaHelper.creaPulsante("Esci", _ -> ParcheggioApp.mostraMenuAdmin(utente));
        Button conferma = InterfacciaHelper.creaPulsante("Conferma Eliminazione", _ -> {
            Utente selezionato = scelta.getValue();
            if (selezionato != null) {
                GestioneUtenti.cancellaUtente(selezionato);
                InterfacciaHelper.mostraConferma("Utente eliminato con successo.");
            } else {
                InterfacciaHelper.mostraErrore("Seleziona un utente prima.");
            }
        });
        VBox layout = new VBox(10, scelta, conferma, esci);
        layout.getStyleClass().add("box-cancellazione");
        layout.setAlignment(Pos.CENTER);
        base.setCenter(layout);
    }

}
