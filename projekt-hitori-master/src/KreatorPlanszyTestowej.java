import java.util.Random;

public class KreatorPlanszyTestowej {
    public static Pole[][] przykladowaPlansza(int rozmiar) {
        Pole[][] tablicaDoZwrocenia = new Pole[rozmiar][rozmiar];

        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                int wartosc = pobierzLosowaWartosc(rozmiar);
                boolean czyKlikniete = pobierzLosowoscKlikniecia();
                Pole pole = new Pole(wartosc, czyKlikniete);
                tablicaDoZwrocenia[i][j] = pole;
            }
        }

        return tablicaDoZwrocenia;
    }

    private static int pobierzLosowaWartosc(int rozmiar) {
        Random rand = new Random();
        return rand.nextInt(rozmiar) + 1;
    }

    private static boolean pobierzLosowoscKlikniecia() {
        Random rand = new Random();
        boolean rezultat = false;

        int klikniete = rand.nextInt(3);
        if (klikniete == 1) {
            rezultat = true;
        }
        return rezultat;
    }
}
