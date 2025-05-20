package listati;

import java.time.LocalDateTime;

// Un record funziona come una classe, senza set, ma solo con get e metodo costruttore
public record Posto(Utente utente, String nome, String cognome, String targa, String tipo, double costo,
                    LocalDateTime dataArrivo, LocalDateTime dataPartenza,
                    int x, int y) {
}
