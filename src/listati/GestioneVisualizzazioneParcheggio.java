package listati;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class GestioneVisualizzazioneParcheggio {
    private static final int GRANDEZZA_Y = 5;

    public static Set<String> getPostiOccupati(LocalDateTime arrivo, LocalDateTime partenza) {
        Set<String> occupati = new HashSet<>();
        for (Posto p : GestioneParcheggio.getPostiPrenotati()) {
            boolean sovrapposto = !partenza.isBefore(p.dataArrivo()) && !arrivo.isAfter(p.dataPartenza());
            if (sovrapposto) {
                occupati.add(p.x() + "," + p.y());
            }
        }
        return occupati;
    }

    public static int calcolaNumeroPosto(int x, int y) {
        return x * GRANDEZZA_Y + y + 1;
    }

    public static boolean isOccupato(int x, int y, Set<String> occupati) {
        return occupati.contains(x + "," + y);
    }
}