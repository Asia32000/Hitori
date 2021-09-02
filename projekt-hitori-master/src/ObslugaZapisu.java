
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ObslugaZapisu {
    private final static String FORMAT_ZAPISU_PLIKU = "yyyyMMdd_HHmm";
    private final static String ROZSZERZENIE_PLANSZY = ".hitori";
    private final static String ROZSZERZENIE_WYDRUKU = ".html";
    private final static String NEW_LINE = "\n";
    private final String separator;
    private final String znakZaznaczenia;

    public ObslugaZapisu(String separator, String znakZaznaczenia) {
        this.separator = separator;
        this.znakZaznaczenia = znakZaznaczenia;
    }

    public void zapiszDoPliku(Pole[][] tablica) throws FileNotFoundException {
        String nazwaPliku = wygenerowanieNazwyPliku();
        PrintWriter zapis = new PrintWriter(nazwaPliku + ROZSZERZENIE_PLANSZY);
        for (Pole[] pola : tablica) {
            for (int m = 0; m < tablica.length; m++) {
                zapiszWartosc(pola[m], zapis);
                zapiszZnakZaznaczeniaJesliKonieczny(pola[m], zapis);
                zapiszSeparatorJesliKonieczny(tablica, m, zapis);
            }
            zapis.print(NEW_LINE);
        }
        zapis.close();
    }

    public void zapiszDoWydruku(Pole[][] tablica) throws IOException {
        String nazwaPliku = wygenerowanieNazwyPliku();
        PrintWriter zapis = new PrintWriter(nazwaPliku + ROZSZERZENIE_WYDRUKU);
        zapis.print("<!DOCTYPE html><html><head><meta charset=UTF-8><style>table, th, td {border: 1px solid black;text-align: center;}</style></head>" +
                "<body><h2>Plansza Hitori</h2><table width=");
        zapis.print(tablica.length * 25);
        zapis.print(">");
        for (Pole[] pola : tablica) {
            zapis.print("<tr>");
            for (int m = 0; m < tablica.length; m++) {
                if (pola[m].czyZaznaczone()) zapis.print("<td bgcolor='black'>");
                else zapis.print("<td>");
                zapiszWartosc(pola[m], zapis);
                zapis.print("</td>");
            }
            zapis.print("</tr>");
        }
        zapis.print("</table></body></html>");
        zapis.close();
        File plik = new File(nazwaPliku + ROZSZERZENIE_WYDRUKU);
        System.out.println(plik.getCanonicalPath());
        String command = "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe";
        Runtime run = Runtime.getRuntime();
        Process proc = run.exec(command + " " + plik.getCanonicalPath());
    }

    private static void zapiszWartosc(Pole pole, PrintWriter zapis) {
        zapis.print(pole.getWartosc());
    }

    private void zapiszZnakZaznaczeniaJesliKonieczny(Pole pole, PrintWriter zapis) {
        if (pole.czyZaznaczone()) {
            zapis.print(znakZaznaczenia);
        }
    }

    private void zapiszSeparatorJesliKonieczny(Pole[][] tablica, int kolumna, PrintWriter zapis) {
        if (kolumna != tablica.length - 1) {
            zapis.print(separator);
        }
    }

    private static String wygenerowanieNazwyPliku() {
        Date todaysDate = new Date();
        DateFormat data = new SimpleDateFormat(FORMAT_ZAPISU_PLIKU);
        try {
            return data.format(todaysDate);

        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        return null;
    }
}
