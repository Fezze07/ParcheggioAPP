package listati;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Consumer;

public class ModificaPrenotazione {
    private static int xPosto = -1;
    private static int yPosto = -1;
    private static BorderPane base;

    public static void setBase(BorderPane root) {
        base = root;
    }

    public static void mostraFinestra(Utente utente, Posto prenotazioneEsistente, boolean opzioniUsate, CheckBox[] opzioni, Consumer<Posto> callbackAggiornamento) {
        Stage stage = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        TextField campoNome = new TextField(prenotazioneEsistente.nome());
        TextField campoCognome = new TextField(prenotazioneEsistente.cognome());
        TextField campoTarga = new TextField(prenotazioneEsistente.targa());
        ComboBox<String> comboTipo = InterfacciaHelper.creaComboTipo();
        comboTipo.setValue(prenotazioneEsistente.tipo());
        DatePicker dataPicker = InterfacciaHelper.creaDatePicker();

        Slider sliderArrivo = InterfacciaHelper.creaSliderOrario(prenotazioneEsistente.dataArrivo());
        Slider sliderPartenza = InterfacciaHelper.creaSliderOrario(prenotazioneEsistente.dataPartenza());
        Label labelArrivo = new Label(InterfacciaHelper.getOrarioFromSlider(sliderArrivo.getValue()));
        Label labelPartenza = new Label(InterfacciaHelper.getOrarioFromSlider(sliderPartenza.getValue()));
        sliderArrivo.valueProperty().addListener((_, _, newVal) -> labelArrivo.setText(InterfacciaHelper.getOrarioFromSlider(newVal.doubleValue())));
        sliderPartenza.valueProperty().addListener((_, _, newVal) -> labelPartenza.setText(InterfacciaHelper.getOrarioFromSlider(newVal.doubleValue())));

        Button scegliPosto = new Button("Controlla Disponibilità Posto");
        scegliPosto.setOnAction(_ -> {
            LocalDate data = dataPicker.getValue();
            LocalDateTime arrivo = InterfacciaHelper.creaDataOrario(data, sliderArrivo);
            LocalDateTime partenza = InterfacciaHelper.creaDataOrario(data, sliderPartenza);
            GUI_VisualizzaParcheggio.scegliPosto(arrivo, partenza, utente, null, postoSelezionato -> {
                xPosto = postoSelezionato[0];
                yPosto = postoSelezionato[1];
            });
        });

        Button conferma = new Button("Conferma Modifiche");
        conferma.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        conferma.setOnAction(_ -> {
            if (!InterfacciaHelper.controllaErrori(campoNome, campoCognome, campoTarga, comboTipo, dataPicker, sliderArrivo, sliderPartenza,
                    new CheckBox(), new CheckBox[0], prenotazioneEsistente.x(), prenotazioneEsistente.y())) return;
            LocalDate data = dataPicker.getValue();
            LocalTime oraArrivo = InterfacciaHelper.minutiToOrario((int) sliderArrivo.getValue());
            LocalTime oraPartenza = InterfacciaHelper.minutiToOrario((int) sliderPartenza.getValue());
            LocalDateTime arrivo = LocalDateTime.of(data, oraArrivo);
            LocalDateTime partenza = LocalDateTime.of(data, oraPartenza);
            double costo = Prezzi.calcolaTotale(
                    comboTipo.getValue(),
                    data,
                    arrivo,
                    partenza,
                    opzioniUsate,
                    opzioni
            );
            Posto aggiornato = new Posto(
                    utente,
                    campoNome.getText(),
                    campoCognome.getText(),
                    campoTarga.getText(),
                    comboTipo.getValue(),
                    costo,
                    arrivo,
                    partenza,
                    xPosto,
                    yPosto
            );
            callbackAggiornamento.accept(aggiornato);
            stage.close();
        });
        layout.getChildren().addAll(
                new Label("Nome"), campoNome,
                new Label("Cognome"), campoCognome,
                new Label("Targa"), campoTarga,
                new Label("Tipo Veicolo"), comboTipo,
                new Label("Data"), dataPicker,
                new Label("Orario Arrivo"), sliderArrivo, labelArrivo,
                new Label("Orario Partenza"), sliderPartenza, labelPartenza,
                scegliPosto,
                conferma
        );
        base.setCenter(layout);
    }
}