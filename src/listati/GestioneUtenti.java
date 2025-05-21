package listati;

import java.util.ArrayList;
import java.util.List;

public class GestioneUtenti {
    private final static List<Utente> databaseUtenti = new ArrayList<>();

    public void aggiungiUtente(Utente a) {
        databaseUtenti.add(a);
        esportaUtenti();
    }

    public static void cancellaUtente(Utente a) {
        databaseUtenti.remove(a);
    }

    public Utente ritornaUtente(String nome) {
        for (Utente u : databaseUtenti) {
            if (u.getNomeUtente().equals(nome)) return u;
        }
        return null;
    }

    public boolean verificaCredenziali(String nomeUtente, String password) {
        for (Utente u : databaseUtenti) {
            if(u.getNomeUtente().equals(nomeUtente))
                if (u.getPassword().equals(password)) {
                    return true;
                } else {
                    InterfacciaHelper.mostraErrore("Password non corretta");
                }
        }
        InterfacciaHelper.mostraErrore("Utente non trovato!");
        return false;
    }

    public boolean verificaDisponibilitaNome(String nome) {
        for (Utente u : databaseUtenti) {
            if (nome.equals(u.getNomeUtente())) return true;
        }
        return false;
    }

    public static void cambiaPassword(Utente a, String nuovaPassword) {
        a.setPassword(nuovaPassword);
    }

    public void cambiaNomeUtente(Utente a, String nuovoNome) {
        a.setNomeUtente(nuovoNome);
    }

    public static void esportaUtenti() {
        SalvaCarica.esportaUtenti(databaseUtenti);
    }

    public static void caricaDatabaseUtenti() {
        List<Utente> caricati = SalvaCarica.caricaUtenti(null);
        databaseUtenti.clear();
        databaseUtenti.addAll(caricati);
        //Aggiunge Admin, Admin, password
        boolean adminPresente = databaseUtenti.stream()
                .anyMatch(u -> u.getNomeUtente().equals("Admin") && u.getPassword().equals("password"));
        if (!adminPresente) {
            databaseUtenti.addFirst(new Utente("Admin", "Admin", "password"));
        }
    }
    public static void cancellaUtenteAdmin(Utente utente) {
        FunzioniAdmin.cancellaUtente(databaseUtenti, utente);
    }
}