import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ObslugaPliku {
    private final static String SEPARATOR = ",";
    private final static String ZNAK_ZAZNACZENIA = "*";

    private ObslugaWczytania obslugaWczytania;
    private ObslugaZapisu obslugaZapisu;

    public ObslugaPliku() {
        obslugaWczytania = new ObslugaWczytania(SEPARATOR, ZNAK_ZAZNACZENIA);
        obslugaZapisu = new ObslugaZapisu(SEPARATOR, ZNAK_ZAZNACZENIA);
    }

    public Integer[][] wczytajPlik(File plik) throws FileNotFoundException {
        return obslugaWczytania.wczytajZPliku(plik);
    }

    public void zapiszPlik(Pole[][] plansza) throws FileNotFoundException {
        obslugaZapisu.zapiszDoPliku(plansza);
    }

    public void zapiszWydruk(Pole[][] plansza) throws IOException {
        obslugaZapisu.zapiszDoWydruku(plansza);
    }
}
