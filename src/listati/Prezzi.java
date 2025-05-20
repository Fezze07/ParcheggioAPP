package listati;

import javafx.geometry.HPos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Prezzi {

    public static int getPrezzo(String tipo) {
        return switch (tipo) {
            case "Auto" -> 5;
            case "Moto" -> 2;
            case "Camion" -> 10;
            case "Corriera" -> 15;
            default -> 0;
        };
    }

    //"Custodia Parcheggio", "Albergo", "Accesso a disabili", "Bagni", "Area Picnic", "Pieno di Benzina/Gasolio/Metano"
    public static int getPrezzoOpzioni(int opzione) {
        return switch (opzione) {
            case 1, 3, 5 -> 5;
            case 2 -> 30;
            case 4 -> 1;
            case 6 -> 50;
            default -> 0;
        };
    }

    public static double getPrezzoOra(LocalDateTime arrivo, LocalDateTime partenza) {
        double totale;
        long differenzaMinuti = java.time.Duration.between(arrivo, partenza).toMinutes();
        totale = (int) (differenzaMinuti / 15) * getCostPrezzo();
        return totale;
    }

    public static double getCostPrezzo() {
        return 0.15;
    }

    public static int getPrezzoGiorno(LocalDate data) {
        DayOfWeek giorno = data.getDayOfWeek();
        return switch (giorno) {
            case DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.TUESDAY -> 1;
            case DayOfWeek.FRIDAY -> 2;
            case DayOfWeek.SATURDAY -> 4;
            case DayOfWeek.SUNDAY -> 3;
        };
    }

    public static double calcolaTotale(String tipo, LocalDate data, LocalDateTime orarioArrivo, LocalDateTime orarioPartenza, boolean extra, CheckBox[] opzioni) {
        int base = getPrezzo(tipo);
        int giorno = (data != null) ? getPrezzoGiorno(data) : 0;
        double ora = (orarioArrivo != null && orarioPartenza != null && orarioPartenza.isAfter(orarioArrivo))
                ? getPrezzoOra(orarioArrivo, orarioPartenza)
                : 0;
        int costoExtra = 0;
        if (extra && opzioni != null) {
            for (int i = 0; i < opzioni.length; i++) {
                if (opzioni[i].isSelected()) {
                    costoExtra += getPrezzoOpzioni(i + 1);
                }
            }
        }
        double totale = base + giorno + ora + costoExtra;
        return arrotonda(totale);
    }

    public static double arrotonda(double valore) {
        return new BigDecimal(valore).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public static GridPane creaListinoPrezzi() {
        GridPane listino = new GridPane();
        listino.getStyleClass().add("listino-grid");
        int riga = 0;
        Label titoloVeicoli = new Label("🚗 Veicoli");
        titoloVeicoli.getStyleClass().add("titolo-sezione");
        listino.add(titoloVeicoli, 0, riga++, 2, 1);
        Label col1 = new Label("Tipo veicolo");
        Label col2 = new Label("Prezzo base (€)");
        col1.getStyleClass().add("intestazione-colonne");
        col2.getStyleClass().add("intestazione-colonne");
        GridPane.setHalignment(col1, HPos.LEFT);
        GridPane.setHalignment(col2, HPos.CENTER);
        listino.add(col1, 0, riga);
        listino.add(col2, 1, riga++);
        String[] tipi = {"🚗 Auto", "🏍 Moto", "🚛 Camion", "🚌 Corriera"};
        for (String tipo : tipi) {
            String tipoClean = tipo.substring(2).trim();
            Label tipoLabel = new Label(tipo);
            Label prezzoLabel = new Label(getPrezzo(tipoClean) + " €");
            GridPane.setHalignment(prezzoLabel, HPos.CENTER);
            listino.add(tipoLabel, 0, riga);
            listino.add(prezzoLabel, 1, riga++);
        }
        riga++;
        Label titoloExtra = new Label("✨ Servizi Extra");
        titoloExtra.getStyleClass().add("titolo-sezione");
        listino.add(titoloExtra, 0, riga++, 2, 1);
        Label colExtra1 = new Label("Servizio");
        Label colExtra2 = new Label("Prezzo extra (€)");
        colExtra1.getStyleClass().add("intestazione-colonne");
        colExtra2.getStyleClass().add("intestazione-colonne");
        GridPane.setHalignment(colExtra2, HPos.CENTER);
        listino.add(colExtra1, 0, riga);
        listino.add(colExtra2, 1, riga++);
        String[] extraNomi = {
                "Custodia Parcheggio", "Albergo", "Accesso a disabili",
                "Bagni", "Area Picnic", "Pieno Benzina/Gasolio/Metano"
        };
        for (int i = 0; i < extraNomi.length; i++) {
            Label nomeLabel = new Label(extraNomi[i]);
            Label prezzoLabel = new Label(getPrezzoOpzioni(i + 1) + " €");
            GridPane.setHalignment(prezzoLabel, HPos.CENTER);
            listino.add(nomeLabel, 0, riga);
            listino.add(prezzoLabel, 1, riga++);
        }
        riga++;
        Label titoloOrario = new Label("🕒 Tariffa Oraria");
        titoloOrario.getStyleClass().add("titolo-sezione");
        listino.add(titoloOrario, 0, riga++, 2, 1);
        listino.add(new Label("Tariffa ogni 15 min:"), 0, riga);
        Label prezzoOra = new Label(getCostPrezzo() + " €");
        GridPane.setHalignment(prezzoOra, HPos.CENTER);
        listino.add(prezzoOra, 1, riga++);
        riga++;
        Label titoloGiorni = new Label("📅 Prezzi per Giorno");
        titoloGiorni.getStyleClass().add("titolo-sezione");
        listino.add(titoloGiorni, 0, riga++, 2, 1);
        String[] giorniITA = {
                "Lunedì", "Martedì", "Mercoledì", "Giovedì",
                "Venerdì", "Sabato", "Domenica"
        };
        for (int i = 0; i < giorniITA.length; i++) {
            Label giornoLabel = new Label(giorniITA[i]);
            LocalDate giornoData = LocalDate.of(2025, 5, 5).with(DayOfWeek.of(i + 1));
            Label prezzoGiorno = new Label(getPrezzoGiorno(giornoData) + " €");
            GridPane.setHalignment(prezzoGiorno, HPos.CENTER);
            listino.add(giornoLabel, 0, riga);
            listino.add(prezzoGiorno, 1, riga++);
        }

        return listino;
    }
}
