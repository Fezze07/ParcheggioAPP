package listati;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

public class InterfacciaHelper {
    private static final int ORARIO_APERTURA = 8 * 60;
    private static final int ORARIO_CHIUSURA = 20 * 60;
    private static BorderPane base;
    public static void setBase(BorderPane root) {
        base = root;
    }

    public static TextField creaCampoTesto(String testo) {
        TextField campo = new TextField();
        campo.getStyleClass().add("campo-testo");
        campo.setPrefWidth(250);
        campo.setMaxWidth(300);
        campo.setPromptText(testo != null ? testo : "");
        return campo;
    }

    public static PasswordField creaCampoPassword(String testo) {
        PasswordField campo = new PasswordField();
        campo.getStyleClass().add("campo-password");
        campo.setPromptText(testo != null ? testo : "");
        campo.setPrefWidth(250);
        campo.setMaxWidth(300);
        return campo;
    }

    public static Label creaLabel(String messaggio) {
        Label label = new Label();
        label.setText(messaggio.toUpperCase());
        label.setPrefWidth(250);
        label.setAlignment(Pos.CENTER);
        label.getStyleClass().setAll("testo");
        return label;
    }

    public static ComboBox<String> creaComboTipo() {
        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().addAll("Auto", "Moto", "Camion", "Corriera");
        combo.setPromptText("Tipo veicolo");
        return combo;
    }

    public static void creaStage(Stage stage, Scene scene) {
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public static Button creaPulsante(String testo, EventHandler<ActionEvent> azione) {
        Button pulsante = new Button(testo);
        pulsante.setOnAction(azione);
        pulsante.getStyleClass().add("pulsanti");
        return pulsante;
    }

    public static DatePicker creaDatePicker() {
        DatePicker data = new DatePicker();
        return bloccaDatePassate(data);
    }

    public static DatePicker bloccaDatePassate(DatePicker picker) {
        picker.setDayCellFactory(_ -> new DateCell() {
            @Override
            public void updateItem(LocalDate giorno, boolean vuoto) {
                super.updateItem(giorno, vuoto);
                if (giorno.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #8B0000; -fx-text-fill: white;");
                } else {
                    setStyle("-fx-background-color: #DFFFE0; -fx-text-fill: black;");
                }
            }
        });
        return picker;
    }

    public static Slider creaSliderOrario(LocalDateTime dataOra) {
        int valoreIniziale = (dataOra != null) ? dataOra.getHour() * 60 + dataOra.getMinute() : ORARIO_APERTURA;
        valoreIniziale = ((valoreIniziale + 7) / 15) * 15;
        Slider slider = new Slider(ORARIO_APERTURA, ORARIO_CHIUSURA, valoreIniziale);
        slider.setBlockIncrement(15);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(60);
        slider.setMinorTickCount(3);
        slider.setSnapToTicks(true);
        return slider;
    }

    public static String formattaOrarioDaMinuti(int minutiTotali) {
        int ore = minutiTotali / 60;
        int minuti = minutiTotali % 60;
        return String.format("%02d:%02d", ore, minuti);
    }

    public static String getOrarioFromSlider(double value) {
        return formattaOrarioDaMinuti((int) value);
    }

    public static LocalTime minutiToOrario(int minuti) {
        return LocalTime.of(minuti / 60, minuti % 60);
    }

    public static LocalDateTime creaDataOrario(LocalDate data, Slider slider) {
        return LocalDateTime.of(data, minutiToOrario((int) slider.getValue()));
    }

    public static String creaStringOrario(LocalDate data, Slider slider) {
        int minutiTotali = (int) slider.getValue();
        String orario = formattaOrarioDaMinuti(minutiTotali);
        return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " alle " + orario;
    }

    public static boolean controllaErrori(TextField nome, TextField cognome, TextField targaCampo, ComboBox<String> tipo,
                                          DatePicker dataPicker, Slider orarioArrivo, Slider orarioPartenza,
                                          CheckBox usaExtra, CheckBox[] extra, int x, int y) {
        if (campoVuoto(nome, cognome, targaCampo)) {
            mostraErrore("Completa tutti i campi!");
            return false;
        }
        if (!targaValida(targaCampo.getText())) {
            mostraErrore("La targa deve essere nel formato AA123BB!");
            return false;
        }
        if (tipo.getValue() == null) {
            mostraErrore("Devi selezionare almeno un tipo di veicolo!");
            return false;
        }
        if (orarioArrivo == null || orarioPartenza == null) {
            mostraErrore("Seleziona sia l'orario di arrivo che quello di partenza!");
            return false;
        }
        LocalDate data = dataPicker.getValue();
        if (data == null) {
            mostraErrore("Seleziona una data!");
            return false;
        }
        LocalDateTime dtArrivo = creaDataOrario(data, orarioArrivo);
        LocalDateTime dtPartenza = creaDataOrario(data, orarioPartenza);
        if (!dtPartenza.isAfter(dtArrivo)) {
            mostraErrore("L'orario di partenza deve essere dopo quello di arrivo!");
            return false;
        }
        if (usaExtra.isSelected() && Stream.of(extra).noneMatch(CheckBox::isSelected)) {
            mostraErrore("Devi selezionare almeno un'opzione extra!");
            return false;
        }
        if (x == -1 && y == -1) {
            mostraErrore("Devi selezionare il posto per il parcheggio!");
            return false;
        }
        return true;
    }

    private static boolean campoVuoto(TextField... campi) {
        return Stream.of(campi).anyMatch(f -> f.getText().isEmpty());
    }

    private static boolean targaValida(String targa) {
        return targa.length() == 7 &&
                targa.substring(0, 2).chars().allMatch(Character::isLetter) &&
                targa.substring(2, 5).chars().allMatch(Character::isDigit) &&
                targa.substring(5).chars().allMatch(Character::isLetter);
    }

    public static void mostraErrore(String messaggio) {
        Alert alert = creaAlert(Alert.AlertType.ERROR, "errore", messaggio);
        alert.setOnShown(_ -> shakeWindow((Stage) alert.getDialogPane().getScene().getWindow()));
        alert.show();
    }

    public static void mostraConferma(String messaggio) {
        Alert alert = creaAlert(Alert.AlertType.INFORMATION, "conferma", messaggio);
        alert.setOnShown(_ -> shakeWindow((Stage) alert.getDialogPane().getScene().getWindow()));
        alert.show();
    }

    public static void mostraConfermaRunnable(String messaggio, Runnable dopo) {
        Alert alert = creaAlert(Alert.AlertType.INFORMATION, "conferma", messaggio);
        alert.setOnHidden(_ -> dopo.run());
        alert.show();
    }

    private static Alert creaAlert(Alert.AlertType tipo, String classeCss, String messaggio) {
        Alert alert = new Alert(tipo);
        personalizzaAlert(alert, classeCss);
        Stage stage = (Stage) base.getScene().getWindow();
        alert.initOwner(stage);
        alert.setTitle(null);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        return alert;
    }

    private static void personalizzaAlert(Alert alert, String classeCss) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStyleClass().add(classeCss);
    }

    private static void shakeWindow(Stage stage) {
        TranslateTransition shake = new TranslateTransition(Duration.millis(70), stage.getScene().getRoot());
        shake.setByX(8);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
    }

    public static VBox creaBoxDataOra() {
        Label orologio = new Label();
        Label data = new Label();
        Animazioni.creaData(orologio, data);
        VBox box = new VBox(5, data, orologio);
        box.setAlignment(Pos.BOTTOM_RIGHT);
        box.setPadding(new Insets(20));
        return box;
    }

    public static void toggleBox(VBox box, boolean visibile) {
        box.setVisible(visibile);
        box.setManaged(visibile);
    }

    public static void autenticazione() {
        effettuaLogout();
    }

    public static void effettuaLogout() {
        GUI_GestioneUtenti.autenticazione(null, utente -> {
            if (utente == null) return;
            if ("Admin".equals(utente.getTipo())) {
                ParcheggioApp.mostraMenuAdmin(utente);
            } else {
                ParcheggioApp.mostraMenuUtente(utente);
            }
        });
    }

    public static HBox creaLabelConIcona(String testoLabel, String nomeFileIcona) {
        ImageView icona = new ImageView(caricaIcona(nomeFileIcona));
        icona.setFitHeight(20);
        icona.setFitWidth(20);
        Label label = new Label(testoLabel);
        label.setPadding(new Insets(0, 0, 0, 5));
        HBox box = new HBox(icona, label);
        box.setAlignment(Pos.CENTER);
        box.setSpacing(15);
        return box;
    }

    public static Image caricaIcona(String nomeFile) {
        String percorso = "file:src/risorse/immagini/" + nomeFile;
        return new Image(percorso);
    }

}
