import java.io.FileNotFoundException;
import java.io.IOException;

public class TestObslugiZapisu {
    public static void main(String[] args) throws IOException {
        int rozmiar = 5;
        Pole[][] plansza = KreatorPlanszyTestowej.przykladowaPlansza(rozmiar);
        ObslugaPliku obslugaPliku = new ObslugaPliku();
        obslugaPliku.zapiszPlik(plansza);
        obslugaPliku.zapiszWydruk(plansza);

        System.out.println("Utworzono nową planszę: ");
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                s.append(String.format("%1$" + (int) (Math.log10(rozmiar) + 2) + "s", plansza[i][j].toString()));
            }
            for (int j = 0; j < Math.log10(rozmiar); j++) {
                s.append("\n");
            }
        }
        System.out.println(s);
    }
}
