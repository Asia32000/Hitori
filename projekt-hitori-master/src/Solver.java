import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Solver {
    public Pole[][] plansza;
    public int rozmiar;

    private Solver(Pole[][] plansza, int rozmiar, boolean zachowaneMozliwosciPola) {
        this.rozmiar = rozmiar;
        this.plansza = new Pole[rozmiar][rozmiar];

        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                this.plansza[i][j] = new Pole(plansza[i][j], this, zachowaneMozliwosciPola);
            }
        }

        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                this.plansza[i][j].setPolaSasiadujace();
            }
        }
    }

    //TESTOWY KONSTRUKTOR!!!!!!! jborysla
    Solver() {
        this.rozmiar = 0;
    }

    Solver(Solver gra) {
        this(gra.plansza, gra.rozmiar, false);
    }

    Solver(Solver gra, boolean zachowaneMozliwosciPola) {
        this(gra.plansza, gra.rozmiar, zachowaneMozliwosciPola);
    }

    Solver(Integer[][] liczby) throws Pole.juzZaznaczoneException {
        this.rozmiar = liczby.length;
        this.plansza = new Pole[rozmiar][rozmiar];

        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                if (liczby[i][j] < 0) {
                    this.plansza[i][j] = new Pole(i, j, -1 * liczby[i][j], this);
                    this.plansza[i][j].setZaznaczone();
                } else this.plansza[i][j] = new Pole(i, j, liczby[i][j], this);
            }
        }

        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                this.plansza[i][j].setPolaSasiadujace();
            }
        }
    }

    Solver(int rozmiar) {
        this.rozmiar = rozmiar;
        this.plansza = new Pole[rozmiar][rozmiar];

        int i, j;

        // Initialization
        for (i = 0; i < rozmiar; i++) {
            for (j = 0; j < rozmiar; j++) {
                this.plansza[i][j] = new Pole(i, j, -1, this);
            }
        }
        for (i = 0; i < rozmiar; i++) {
            for (j = 0; j < rozmiar; j++) {
                this.plansza[i][j].setPolaSasiadujace();
            }
        }

        this.utworzUkladCzarnych();

        Solver rozwiazanie = utworzeniePlanszy(this);

        if (rozwiazanie != null) {
            this.plansza = rozwiazanie.plansza;

            this.getZaznaczonePola().forEach(cell -> cell.setWartosc(ThreadLocalRandom.current().nextInt(1, rozmiar + 1)));

            for (i = 0; i < rozmiar; i++) {
                for (j = 0; j < rozmiar; j++) {
                    this.plansza[i][j].przywrocStanPola();
                }
            }
        }
    }

    private void utworzUkladCzarnych() {

        List<Pole> nieokreslonePola = new ArrayList<>(this.getNieokreslonePola());
        Collections.shuffle(nieokreslonePola);
        for (Pole c : nieokreslonePola) {
            try {
                c.setUtworzenieCzarnych();
            } catch (Pole.juzZaznaczoneException | NiepoprawnyStanException e) {
                c.przywrocStanPola();
            }
        }

        for (Pole c : this.getNiezaczernionePola()) {
            c.przywrocStanPola();
        }
    }

    private static Solver utworzeniePlanszy(Solver state) {

        Stack<Solver> stos = new Stack<>();
        Set<Solver> sprawdzone = new HashSet<>();
        stos.push(state);
        while (!stos.empty()) {
            Solver element = stos.pop();
            sprawdzone.add(element);

            try {
                element.tworzenieWnioskowania();
            } catch (NiepoprawnyStanException e) {
                continue;
            }

//            System.out.println("I'm trying with ");
//            System.out.println(element);

            if (element.czyRozwiazane() && element.niePosiadaNieoznaczonePola()) {
                return element;
            }

            Set<Solver> opcjeNastepnegoKroku = element.WyznaczanieOpcjiNastepnegoKroku();

            for (Solver g : opcjeNastepnegoKroku) {
                if (!sprawdzone.contains(g)) stos.push(g);
            }
        }

        return null;
    }

    private boolean niePosiadaNieoznaczonePola() {
        return this.getNieokresloneNiezaczernionePola().size() == 0;
    }

    Pole[][] getPlansza() {
        return plansza;
    }

    int getRozmiar() {
        return rozmiar;
    }

    private boolean czyPoprawne() {
        return !czyZaznaczonePolaPolaczoneWrzedach() && !czyZaznaczonePolePolaczoneWkolumnach() && this.czyPolaczone();
    }

    boolean czyZaznaczonePolePolaczoneWkolumnach() {
        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar - 1; j++) {
                if (plansza[j][i].czyZaznaczone() && plansza[j + 1][i].czyZaznaczone()) return true;
            }
        }
        return false;
    }

    boolean czyZaznaczonePolaPolaczoneWrzedach() {
        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar - 1; j++) {
                if (plansza[i][j].czyZaznaczone() && plansza[i][j + 1].czyZaznaczone()) return true;
            }
        }
        return false;
    }

    boolean czyPolaczone() {
        Set<Pole> niezaczernionePola = getNiezaczernionePola();
        Set<Pole> osiagalnePola = niezaczernionePola.iterator().next().getOsiagalnePola();
        resetSprawdzonePola();
        return niezaczernionePola.equals(osiagalnePola);
    }

    private void resetSprawdzonePola() {
        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                plansza[i][j].setSprawdzone(false);
            }
        }
    }

    private Set<Pole> getNiezaczernionePola() {
        Set<Pole> niezaczernionePola = new HashSet<>();
        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                if (!plansza[i][j].czyZaznaczone()) niezaczernionePola.add(plansza[i][j]);
            }
        }
        return niezaczernionePola;
    }

    Set<Pole> getZaznaczonePola() {
        Set<Pole> ZaczernionePola = new HashSet<>();
        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                if (plansza[i][j].czyZaznaczone()) ZaczernionePola.add(plansza[i][j]);
            }
        }
        return ZaczernionePola;
    }

    private List<Pole> getNiepoprawneCzarnePola() {
        Set<Pole> zaznaczonePola = getZaznaczonePola();
        Set<Pole> niepoprawneCzarnePola = new HashSet<>();
        for (Pole b : zaznaczonePola) {
            if (b.getPolaSasiadujace().stream().anyMatch(Pole::czyZaznaczone)) {
                niepoprawneCzarnePola.add(b);
            } else if (!this.czyPolaczone()) {
                Solver g = new Solver(this);
                g.plansza[b.getX()][b.getY()].przywrocStanPola();
                if (g.czyPolaczone()) niepoprawneCzarnePola.add(b);
            }
        }
        List<Pole> l = new ArrayList<>(niepoprawneCzarnePola);
        Collections.shuffle(l);
        return l;
    }

    private Set<Pole> getNieokreslonePola() {
        Set<Pole> nieokreslonePola = new HashSet<>();
        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                if (!plansza[i][j].czyZaznaczone() && !plansza[i][j].czyNiezaznaczone())
                    nieokreslonePola.add(plansza[i][j]);
            }
        }
        return nieokreslonePola;
    }

    boolean powtorzeniaWkolumnach() {
        Set<Integer> liczby;
        for (int i = 0; i < rozmiar; i++) {
            liczby = new HashSet<>();
            for (int j = 0; j < rozmiar; j++) {
                if (plansza[j][i].czyZaznaczone()) continue;
                Integer liczba = plansza[j][i].getWartosc();
                if (liczby.contains(liczba)) return true;
                else liczby.add(liczba);
            }
        }
        return false;

    }

    boolean powtorzeniaWrzedach() {
        Set<Integer> liczby;
        for (int i = 0; i < rozmiar; i++) {
            liczby = new HashSet<>();
            for (int j = 0; j < rozmiar; j++) {
                if (plansza[i][j].czyZaznaczone()) continue;
                Integer liczba = plansza[i][j].getWartosc();
                if (liczby.contains(liczba)) return true;
                else liczby.add(liczba);
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                s.append(String.format("%1$" + (int) (Math.log10(rozmiar) + 2) + "s", plansza[i][j].toString()));
            }
            for (int j = 0; j < Math.log10(rozmiar); j++) {
                s.append("\n");
            }
        }
        return String.valueOf(s);
    }


    Set<Solver> getOpcjeNastepnegoKroku() {
        Set<Solver> opcje = new HashSet<>();
        Set<Pole> opcjePola = getNieokreslonePola();
        for (Pole c : opcjePola) {
            try {
                Solver g = c.setCzarnePobierzStan();
                opcje.add(g);
            } catch (NiepoprawnyStanException ignored) {
            }
        }
        return opcje;
    }

    private Set<Solver> WyznaczanieOpcjiNastepnegoKroku() {
        Set<Solver> opcje = new HashSet<>();
        List<Pole> opcjePola = this.getNiezaczernionePola().stream().filter(cell -> cell.getWartosc() == -1).collect(Collectors.toList());
        for (Pole c : opcjePola) {
            List<Solver> mozliweRozwiazania = c.ustawMozliweDoPrzypisaniaWartosciPobierzStan();
            opcje.addAll(mozliweRozwiazania);
        }
        return opcje;
    }

    boolean czyRozwiazane() {
        return this.czyPoprawne() && !this.powtorzeniaWrzedach() && !this.powtorzeniaWkolumnach();
    }

    void wnioskowanie() throws NiepoprawnyStanException {
        Solver original = new Solver(this);
        this.wnioskowanieSasiadujacychPol();
        this.wnioskowanieSekwencjiXYX();
        this.wnioskowanieSekwencjiXXYZX();
        if (!this.equals(original)) {
            this.wnioskowanie();
        }
    }

    void zmienPole(int x, int y) {
        if (this.plansza[x][y].zaznaczone) {
            this.plansza[x][y].zaznaczone = false;
            this.plansza[x][y].niezaznaczone = true;
        } else {
            this.plansza[x][y].zaznaczone = true;
            this.plansza[x][y].niezaznaczone = false;
        }
    }

    public static Solver wyczysc(Solver g) {
        Solver czysta = new Solver(g);
        for (int i = 0; i < czysta.rozmiar; i++) {
            for (int j = 0; j < czysta.rozmiar; j++) {
                czysta.plansza[i][j].przywrocStanPola();
            }
        }
        return czysta;
    }

    private void tworzenieWnioskowania() throws NiepoprawnyStanException {
        Solver original = new Solver(this);

        this.brakNastepnychKrokow();
        this.ostatniKrok();

        if (!this.equals(original)) this.tworzenieWnioskowania();
    }

    private void ostatniKrok() throws NiepoprawnyStanException {

        for (Pole c : this.getNieokresloneNiezaczernionePola()) {
            if (c.getMozliweWartosci().size() == 1) {
//                if (c.getPossibleValues().get(0).equals(this.value)) throw new GameState.ImpossibleStateException();
                try {
                    c.setWartoscOrazOdznaczPole(c.getMozliweWartosci().get(0));
                } catch (Pole.juzZaznaczoneException e) {
                    throw new NiepoprawnyStanException();
                }
            }
        }
    }

    private void brakNastepnychKrokow() throws NiepoprawnyStanException {
        for (Pole c : this.getNieokresloneNiezaczernionePola()) {
            if (c.getMozliweWartosci().size() == 0) {
                throw new NiepoprawnyStanException();
            }
        }
    }

    private Set<Pole> getNieokresloneNiezaczernionePola() {
        return this.getNiezaczernionePola().stream().filter(cell -> cell.getWartosc() == -1).collect(Collectors.toSet());
    }

    private void wnioskowanieSasiadujacychPol() throws NiepoprawnyStanException {
        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                try {
                    Pole c = plansza[i][j];
                    Set<Pole> cSasiedniePola = c.getPolaSasiadujace();
                    if (cSasiedniePola.stream().filter(Pole::czyZaznaczone).count() == cSasiedniePola.size() - 1) {
                        for (Pole n : cSasiedniePola) {
                            if (!n.czyZaznaczone()) n.setNiezaznaczone();
                        }
                    }
                } catch (Pole.juzZaznaczoneException ignored) {
                    throw new NiepoprawnyStanException();
                }
            }
        }
    }

    private void wnioskowanieSekwencjiXYX() throws NiepoprawnyStanException {
        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar - 2; j++) {
                try {
                    if (plansza[i][j].getWartosc() == plansza[i][j + 2].getWartosc())
                        plansza[i][j + 1].setNiezaznaczone();
                    if (plansza[j][i].getWartosc() == plansza[j + 2][i].getWartosc())
                        plansza[j + 1][i].setNiezaznaczone();
                } catch (Pole.juzZaznaczoneException ignored) {
                    throw new NiepoprawnyStanException();
                }
            }
        }
    }

    private void wnioskowanieSekwencjiXXYZX() throws NiepoprawnyStanException {
        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar - 1; j++) {
                try {
                    if (plansza[i][j].getWartosc() == plansza[i][j + 1].getWartosc()) {
                        for (Pole c : plansza[i][j].getRzad()) {
                            if (!c.equals(plansza[i][j]) && !c.equals(plansza[i][j + 1]) && c.getWartosc() == plansza[i][j].getWartosc())
                                c.setZaznaczone();
                        }
                    }
                    if (plansza[j][i].getWartosc() == plansza[j + 1][i].getWartosc()) {
                        for (Pole c : plansza[j][i].getKolumna()) {
                            if (!c.equals(plansza[j][i]) && !c.equals(plansza[j + 1][i]) && c.getWartosc() == plansza[j][i].getWartosc())
                                c.setZaznaczone();
                        }
                    }
                } catch (Pole.juzZaznaczoneException ignored) {
                    throw new NiepoprawnyStanException();
                }
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Solver)) {
            return false;
        }
        Solver g = (Solver) obj;
        if (g.rozmiar != this.rozmiar) return false;

        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                Pole thisPole = this.plansza[i][j];
                Pole gPole = g.plansza[i][j];
                if (!thisPole.equals(gPole)) {
                    return false;
                }
            }
        }
        return true;
    }

    static void podpowiedz(Pole[][] plansza1, Pole[][] plansza2, JLabel info) {
        //System.out.println("Wszystkie możliwe ruchy do wykonania: ");
        label:
        for (int i = 0; i < plansza1.length; i++) {
            for (int j = 0; j < plansza1.length; j++) {
                if (!plansza1[i][j].equals(plansza2[i][j])) {
                    if (plansza1[i][j].czyZaznaczone()) {
                        int a = i + 1;
                        int b = j + 1;
                        System.out.println("Pole o współrzędnych x = " + b + " y = " + a + " nie powinno być zaznaczone");
                        info.setText("Pole o współrzędnych x = " + b + " y = " + a + " nie powinno być zaznaczone");
                        break label;
                    } else {
                        int a = i + 1;
                        int b = j + 1;
                        System.out.println("Należy zaznaczyć pole o współrzędnych x = " + b + " y = " + a);
                        info.setText("Należy zaznaczyć pole o współrzędnych x = " + b + " y = " + a);
                        break label;
                    }
                }
            }
        }
    }

    static class NiepoprawnyStanException extends Throwable {
    }
}