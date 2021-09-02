import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ObslugaWczytania {

    private final String separator;
    private final String znakZaznaczenia;

    public ObslugaWczytania(String separator, String znakZaznaczenia) {
        this.separator = separator;
        this.znakZaznaczenia = znakZaznaczenia;
    }

    public Integer[][] wczytajZPliku(File plik) throws FileNotFoundException {
        String dane;
        String[] wiersz;
        int w = 0;
        Scanner myReader = new Scanner(plik);
        dane = myReader.nextLine();
        wiersz = dane.split(separator);
        int rozmiar = wiersz.length;
        Integer[][] liczby = new Integer[rozmiar][rozmiar];
        for (int i = 0; i < wiersz.length; i++) {
            if (wiersz[i].endsWith(znakZaznaczenia)) {
                liczby[w][i] = Integer.valueOf(wiersz[i].substring(0, wiersz[i].indexOf(znakZaznaczenia)));
                liczby[w][i] = liczby[w][i] * -1;
            } else liczby[w][i] = Integer.valueOf(wiersz[i]);
        }

        while (myReader.hasNextLine()) {
            w++;
            dane = myReader.nextLine();
            wiersz = dane.split(separator);
            for (int i = 0; i < wiersz.length; i++) {
                if (wiersz[i].endsWith(znakZaznaczenia)) {
                    liczby[w][i] = Integer.valueOf(wiersz[i].substring(0, wiersz[i].indexOf(znakZaznaczenia)));
                    liczby[w][i] = liczby[w][i] * -1;
                } else liczby[w][i] = Integer.valueOf(wiersz[i]);
            }
        }
        return liczby;
    }
}
