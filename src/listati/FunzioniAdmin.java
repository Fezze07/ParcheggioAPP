package listati;

import javafx.geometry.Insets;
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
        // ====== SEZIONE VEICOLI ======
        TitledPane sezioneVeicoli = creaSezione(
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
        sezioneVeicoli.getStyleClass().add("sezione-veicoli");
        // ====== SEZIONE OPZIONI EXTRA ======
        TitledPane sezioneOpzioni = creaSezione(
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
        sezioneOpzioni.getStyleClass().add("sezione-opzioni-extra");
        // ====== SEZIONE GIORNI ======
        TitledPane sezioneGiorni = creaSezione(
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
        sezioneGiorni.getStyleClass().add("sezione-giorni");
        // ====== SEZIONE COSTO ORARIO ======
        Label labelOrario = new Label("Costo ogni 15 minuti:");
        TextField campoOrario = new TextField(String.valueOf(Prezzi.costoOrario));
        labelOrario.getStyleClass().add("etichetta-orario");
        campoOrario.getStyleClass().add("campo-costo-orario");

        HBox orarioBox = new HBox(10, labelOrario, campoOrario);
        orarioBox.setAlignment(Pos.CENTER);
        orarioBox.getStyleClass().add("contenitore-orario");

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
        VBox contenitoreTariffe = new VBox(20);
        contenitoreTariffe.setAlignment(Pos.CENTER);
        contenitoreTariffe.getStyleClass().add("contenitore-tariffe");
        contenitoreTariffe.getChildren().addAll(sezioneVeicoli, sezioneOpzioni, sezioneGiorni, orarioBox, salva, esci);

        VBox.setMargin(sezioneVeicoli, new Insets(0, 0, 0, 0));
        sezioneVeicoli.setMaxWidth(450);
        sezioneVeicoli.setPrefWidth(450);
        sezioneOpzioni.setMaxWidth(450);
        sezioneOpzioni.setPrefWidth(450);
        sezioneGiorni.setMaxWidth(450);
        sezioneGiorni.setPrefWidth(450);

        base.setCenter(contenitoreTariffe);
    }

    private static <T> TitledPane creaSezione(String titolo, Function<T, Integer> getter, BiConsumer<T, Integer> setter, Map<T, String> etichette) {
        GridPane grigliaSezione = new GridPane();
        grigliaSezione.setHgap(15);
        grigliaSezione.setVgap(12);
        grigliaSezione.getStyleClass().add("griglia-sezione");
        int riga = 0;
        for (T chiave : etichette.keySet()) {
            Label etichettaPrezzo = new Label(etichette.get(chiave) + ":");
            etichettaPrezzo.getStyleClass().add("etichetta-selezione");
            TextField campoPrezzo = new TextField(String.valueOf(getter.apply(chiave)));
            campoPrezzo.getStyleClass().add("campo-selezione");

            grigliaSezione.add(etichettaPrezzo, 0, riga);
            grigliaSezione.add(campoPrezzo, 1, riga);
            campoPrezzo.textProperty().addListener((_, _, newVal) -> {
                try {
                    int valore = Integer.parseInt(newVal);
                    setter.accept(chiave, valore);
                } catch (NumberFormatException e) {
                    InterfacciaHelper.mostraErrore("Inserisci un formato valido!");
                }
            });
            riga++;
        }
        TitledPane pane = new TitledPane(titolo, grigliaSezione);
        pane.getStyleClass().add("pannello");
        pane.setExpanded(false);
        return pane;
    }

    public static void modificaApertura(Utente utente) {
        TextField campoApertura = InterfacciaHelper.creaCampoTesto("Apertura = " +InterfacciaHelper.getApertura());
        TextField campoChiusura = InterfacciaHelper.creaCampoTesto("Chiusura = " +InterfacciaHelper.getChiusura());
        VBox contenutoOrari = new VBox(10);
        contenutoOrari.setPadding(new Insets(10));
        contenutoOrari.getChildren().addAll(
                InterfacciaHelper.creaLabel("Orario di apertura:"), campoApertura,
                InterfacciaHelper.creaLabel("Orario di chiusura:"), campoChiusura
        );
        TitledPane orari = new TitledPane("Orari Apertura", contenutoOrari);
        orari.getStyleClass().add("sezione-orari");
        orari.setExpanded(true);

        Button salva = InterfacciaHelper.creaPulsante("Salva Tutto", _ -> {
            try {
                double oraApertura = Double.parseDouble(campoApertura.getText().trim());
                double oraChiusura = Double.parseDouble(campoChiusura.getText().trim());
                if (oraApertura < 0 || oraApertura > 23 || oraChiusura < 0 || oraChiusura > 23) {
                    InterfacciaHelper.mostraErrore("Gli orari devono essere tra 0 e 23.");
                    return;
                }
                if (oraApertura >= oraChiusura) {
                    InterfacciaHelper.mostraErrore("L'orario di apertura deve essere prima di quello di chiusura.");
                    return;
                }
                InterfacciaHelper.setOrariParcheggi(oraApertura, oraChiusura);
                InterfacciaHelper.mostraConferma("Orari aggiornati con successo!");
            } catch (Exception e) {
                InterfacciaHelper.mostraErrore("Errore durante il salvataggio degli orari.");
            }
        });
        Button esci = InterfacciaHelper.creaPulsante("Esci", _ -> ParcheggioApp.mostraMenuAdmin(utente));
        VBox contenitore = new VBox(20);
        contenitore.setAlignment(Pos.CENTER);
        contenitore.getStyleClass().add("contenitore-tariffe");
        contenitore.getChildren().addAll(orari, salva, esci);
        base.setCenter(contenitore);
    }
}
