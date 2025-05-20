package listati;

import java.io.*;
import java.net.URISyntaxException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SalvaCarica {
    public static void esportaParcheggio(List<Posto> prenotazioni) {
        if (prenotazioni == null || prenotazioni.isEmpty()) {
            InterfacciaHelper.mostraErrore("Nessuna prenotazione da esportare!");
            return;
        }
        try {
            String pathFile = GestoreDatabase.getPathDatabase("parcheggio.csv");
            File file = new File(pathFile);
            try (FileWriter writer = new FileWriter(file)) {
                // Scriviamo l'intestazione del CSV
                writer.append("Utente,Nome,Cognome,Targa,Tipo,Data Arrivo,Orario Arrivo,Data Partenza,Orario Partenza,Costo,x,y\n");
                for (Posto p : prenotazioni) {
                    String riga = String.join(",",
                            p.utente().getTipo(),
                            p.utente().getNomeUtente(),
                            p.utente().getPassword(),
                            p.nome(),
                            p.cognome(),
                            p.targa(),
                            p.tipo(),
                            p.dataArrivo().toLocalDate().toString(),
                            p.dataArrivo().toLocalTime().toString(),
                            p.dataPartenza().toLocalDate().toString(),
                            p.dataPartenza().toLocalTime().toString(),
                            String.format(Locale.US, "%.2f", p.costo()),
                            String.valueOf(p.x()),
                            String.valueOf(p.y())
                    );
                    writer.append(riga).append("\n");
                }
            }
        } catch (IOException | URISyntaxException e) {
            InterfacciaHelper.mostraErrore("Errore nell'esportazione del report:\n" + e.getMessage());
        }
    }

    public static List<Posto> caricaParcheggio(File fileSelezionato) {
        List<Posto> prenotazioni = new ArrayList<>();
        List<String> righeValide = new ArrayList<>();
        BufferedReader reader = null;
        boolean fileCaricato = fileSelezionato != null;
        try {
            InputStream is;
            if (fileCaricato) {
                is = new FileInputStream(fileSelezionato);
            } else {
                String pathFile = GestoreDatabase.getPathDatabase("parcheggio.csv");
                File file = new File(pathFile);
                if (!file.exists()) return prenotazioni;
                is = new FileInputStream(file);
            }
            reader = new BufferedReader(new InputStreamReader(is));
            String intestazione = reader.readLine();
            if (intestazione == null) {
                InterfacciaHelper.mostraErrore("Il file CSV è vuoto.");
                return prenotazioni;
            }
            righeValide.add(intestazione);
            String riga;
            while ((riga = reader.readLine()) != null) {
                String[] campi = riga.split(",");
                if (campi.length != 14) {
                    System.err.println("Riga malformata in parcheggio: " + riga);
                    continue;
                }
                try {
                    Utente utente = new Utente(campi[0], campi[1], campi[2]);
                    String nome = campi[3];
                    String cognome = campi[4];
                    String targa = campi[5];
                    String tipo = campi[6];
                    LocalDateTime dataArrivo = LocalDateTime.of(
                            LocalDate.parse(campi[7]),
                            LocalTime.parse(campi[8])
                    );
                    LocalDateTime dataPartenza = LocalDateTime.of(
                            LocalDate.parse(campi[9]),
                            LocalTime.parse(campi[10])
                    );
                    if (dataPartenza.isBefore(LocalDateTime.now())) {
                        continue;
                    }
                    double costo = Double.parseDouble(campi[11]);
                    int x = Integer.parseInt(campi[12]);
                    int y = Integer.parseInt(campi[13]);
                    Posto p = new Posto(utente, nome, cognome, targa, tipo, costo, dataArrivo, dataPartenza, x, y);
                    prenotazioni.add(p);
                    righeValide.add(riga);
                } catch (Exception e) {
                    System.err.println("Riga saltata in parcheggio: " + riga);
                }
            }
            if (fileCaricato) {
                try (PrintWriter writer = new PrintWriter(fileSelezionato)) {
                    for (String r : righeValide) {
                        writer.println(r);
                    }
                }
            }
        } catch (Exception e) {
            InterfacciaHelper.mostraErrore("Errore nel caricamento:\n" + e.getMessage());
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                System.err.println("Errore nella chiusura del file.");
            }
        }
        return prenotazioni;
    }

    public static void esportaUtenti(List<Utente> databaseUtenti) {
        if (databaseUtenti == null || databaseUtenti.isEmpty()) {
            InterfacciaHelper.mostraErrore("Nessun utente all'interno da esportare!");
            return;
        }
        try {
            String pathFile = GestoreDatabase.getPathDatabase("databaseUtenti.csv");
            File file = new File(pathFile);
            try (FileWriter writer = new FileWriter(file)) {
                writer.append("Tipo,NomeUtente,Password\n");
                for (Utente a : databaseUtenti) {
                    String riga = String.join(",",
                            a.getTipo(),
                            a.getNomeUtente(),
                            a.getPassword()
                    );
                    writer.append(riga).append("\n");
                }
            }
        } catch (IOException | URISyntaxException e) {
            InterfacciaHelper.mostraErrore("Errore nell'esportazione del database:\n" + e.getMessage());
        }
    }

    public static List<Utente> caricaUtenti(File fileSelezionato) {
        List<Utente> database = new ArrayList<>();
        BufferedReader reader = null;
        try {
            InputStream is;
            if (fileSelezionato != null) {
                is = new FileInputStream(fileSelezionato);
            } else {
                String pathFile = GestoreDatabase.getPathDatabase("databaseUtenti.csv");
                File file = new File(pathFile);
                if (!file.exists()) return database;
                is = new FileInputStream(file);
            }
            reader = new BufferedReader(new InputStreamReader(is));
            String riga = reader.readLine();
            if (riga == null) {
                return database;
            }
            while ((riga = reader.readLine()) != null) {
                String[] campi = riga.split(",");
                if (campi.length != 3) {
                    System.err.println("Riga malformata in utenti: " + riga);
                    continue;
                }
                try {
                    Utente a = new Utente(campi[0], campi[1], campi[2]);
                    database.add(a);
                } catch (Exception e) {
                    System.err.println("Riga saltata in utenti: " + riga);
                }
            }
        } catch (Exception e) {
            InterfacciaHelper.mostraErrore("Errore nel caricamento:\n" + e.getMessage());
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                System.err.println("Errore nella chiusura del file.");
            }
        }
        return database;
    }

    public static void salvaPrezzi() {
        try {
            String pathFile = GestoreDatabase.getPathDatabase("tariffe.txt");
            File file = new File(pathFile);
            try (FileWriter writer = new FileWriter(file)) {
                StringBuilder sb = new StringBuilder();
                sb.append(Prezzi.costoOrario).append("\n");
                Prezzi.prezziBaseVeicoli.forEach((k, v) -> sb.append("VEICOLO;").append(k).append(";").append(v).append("\n"));
                Prezzi.prezziOpzioni.forEach((k, v) -> sb.append("OPZIONE;").append(k).append(";").append(v).append("\n"));
                Prezzi.prezziGiorni.forEach((k, v) -> sb.append("GIORNO;").append(k).append(";").append(v).append("\n"));
                writer.write(sb.toString());
            }
        } catch (IOException | URISyntaxException e) {
            InterfacciaHelper.mostraErrore("Errore nell'esportazione delle tariffe:\n" + e.getMessage());
        }
    }

    public static void caricaPrezzi() {
        try {
            String pathFile = GestoreDatabase.getPathDatabase("tariffe.txt");
            File file = new File(pathFile);
            if (!file.exists()) return;
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                Prezzi.costoOrario = Double.parseDouble(reader.readLine());
                Prezzi.prezziBaseVeicoli.clear();
                Prezzi.prezziOpzioni.clear();
                Prezzi.prezziGiorni.clear();
                String riga;
                while ((riga = reader.readLine()) != null) {
                    String[] campi = riga.split(";");
                    if (campi.length != 3) continue;
                    String tipo = campi[0];
                    String chiave = campi[1];
                    int valore = Integer.parseInt(campi[2]);
                    switch (tipo) {
                        case "VEICOLO" -> Prezzi.prezziBaseVeicoli.put(chiave, valore);
                        case "OPZIONE" -> Prezzi.prezziOpzioni.put(Integer.parseInt(chiave), valore);
                        case "GIORNO" -> Prezzi.prezziGiorni.put(DayOfWeek.valueOf(chiave), valore);
                    }
                }
            }
        } catch (IOException | URISyntaxException | NumberFormatException e) {
            InterfacciaHelper.mostraErrore("Errore nel caricamento tariffe:\n" + e.getMessage());
        }
    }
}