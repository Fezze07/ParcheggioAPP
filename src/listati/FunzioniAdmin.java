package listati;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;


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
                    setText("Utente: " + p.utente() +
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

    public static void modificaTariffe(Utente utente) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("box-admin");

        // ====== SEZIONE VEICOLI ======
        TitledPane sezioneVeicoli = creaSezionePrezzi(
                "Prezzi Veicoli",
                Prezzi::getPrezzo,
                Prezzi::setPrezzoVeicolo,
                Map.of(
                        "Auto", "Auto",
                        "Moto", "Moto",
                        "Camion", "Camion",
                        "Corriera", "Corriera"
                )
        );
        // ====== SEZIONE OPZIONI EXTRA ======
        TitledPane sezioneOpzioni = creaSezionePrezzi(
                "Prezzi Opzioni Extra",
                Prezzi::getPrezzoOpzioni,
                Prezzi::setPrezzoOpzione,
                Map.of(
                        1, "Custodia Parcheggio",
                        2, "Albergo",
                        3, "Accesso a disabili",
                        4, "Bagni",
                        5, "Area Picnic",
                        6, "Pieno carburante"
                )
        );
        // ====== SEZIONE GIORNI ======
        TitledPane sezioneGiorni = creaSezionePrezzi(
                "Prezzi Giornalieri",
                g -> Prezzi.getPrezzoGiorno(DayOfWeek.valueOf(g)),
                (g, p) -> Prezzi.setPrezzoGiorno(DayOfWeek.valueOf(g), p),
                Map.of(
                        "MONDAY", "Lunedì",
                        "TUESDAY", "Martedì",
                        "WEDNESDAY", "Mercoledì",
                        "THURSDAY", "Giovedì",
                        "FRIDAY", "Venerdì",
                        "SATURDAY", "Sabato",
                        "SUNDAY", "Domenica"
                )
        );
        // ====== SEZIONE COSTO ORARIO ======
        Label labelOrario = new Label("Costo ogni 15 minuti:");
        TextField campoOrario = new TextField(String.valueOf(Prezzi.costoOrario));
        HBox orarioBox = new HBox(10, labelOrario, campoOrario);
        orarioBox.setAlignment(Pos.CENTER);

        // ====== PULSANTI ======
        Button salva = InterfacciaHelper.creaPulsante("Salva Tutto", _ -> {
            try {
                Prezzi.setCostoOrario(Double.parseDouble(campoOrario.getText()));
                SalvaCarica.salvaPrezzi();
                InterfacciaHelper.mostraConferma("Tutte le tariffe sono state aggiornate con successo!");
            } catch (NumberFormatException e) {
                InterfacciaHelper.mostraErrore("Il costo orario deve essere un numero valido.");
            }
        });

        Button esci = InterfacciaHelper.creaPulsante("Esci", _ -> ParcheggioApp.mostraMenuAdmin(utente));
        root.getChildren().addAll(sezioneVeicoli, sezioneOpzioni, sezioneGiorni, orarioBox, salva, esci);
        base.setCenter(root);
    }

    private static <T> TitledPane creaSezionePrezzi(String titolo, Function<T, Integer> getter, BiConsumer<T, Integer> setter, Map<T, String> etichette) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        int riga = 0;
        for (T chiave : etichette.keySet()) {
            Label label = new Label(etichette.get(chiave) + ":");
            TextField campo = new TextField(String.valueOf(getter.apply(chiave)));
            grid.add(label, 0, riga);
            grid.add(campo, 1, riga);
            campo.textProperty().addListener((_, _, newVal) -> {
                try {
                    int valore = Integer.parseInt(newVal);
                    setter.accept(chiave, valore);
                } catch (NumberFormatException e) {
                    InterfacciaHelper.mostraErrore("Inserisci un formato valido!");
                }
            });
            riga++;
        }
        TitledPane pane = new TitledPane(titolo, grid);
        pane.setExpanded(false);
        return pane;
    }
}
