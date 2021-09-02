import java.util.*;

public class Pole {
    public int wartosc;
    private final int x;
    private final int y;
    private final Solver stanGry;
    private final Set<Pole> polaSasiadujace = new HashSet<>();
    public boolean zaznaczone = false;
    public boolean niezaznaczone = false;
    private boolean sprawdzone = false;
    private List<Integer> mozliweWartosci = new ArrayList<>();

    Pole(Pole pole, Solver stanGry, boolean zachowajMozliwosciPola) {
        this.wartosc = pole.wartosc;
        this.x = pole.x;
        this.y = pole.y;
        this.zaznaczone = pole.zaznaczone;
        this.niezaznaczone = pole.niezaznaczone;
        this.stanGry = stanGry;
        if (!zachowajMozliwosciPola) {
            for (int k = 0; k < stanGry.getRozmiar(); k++) {
                this.mozliweWartosci.add(k + 1);
            }
        } else {
            this.mozliweWartosci = new ArrayList<>(pole.mozliweWartosci);
        }
    }

    Pole(Pole pole, Solver stanGry) {
        this(pole, stanGry, false);
    }

    Pole(int i, int j, int wartosc, Solver stanGry) {
        this.x = i;
        this.y = j;
        this.wartosc = wartosc;
        this.stanGry = stanGry;
        for (int k = 0; k < stanGry.getRozmiar(); k++) {
            mozliweWartosci.add(k + 1);
        }
    }

    Pole(int i, int j, int wartosc, boolean zaznaczone, Solver stanGry) {
        this(i, j, wartosc, stanGry);
        this.zaznaczone = zaznaczone;
    }

    // KONSTRUKTOR TYLKO DO TESTOW!!!!!!!!!!!! jborysla
    Pole(int wartosc, boolean klikniete) {
        this.x = 0;
        this.y = 0;
        this.stanGry = new Solver();
        this.wartosc = wartosc;
        this.zaznaczone = klikniete;
        if (!this.zaznaczone) {
            this.niezaznaczone = true;
        }

    }

    int getWartosc() {
        return wartosc;
    }

    boolean czyZaznaczone() {
        return zaznaczone;
    }

    void setZaznaczone() throws juzZaznaczoneException {
        if (this.zaznaczone) {
            return;
        }
        if (this.niezaznaczone) {
            throw new juzZaznaczoneException();
        } else {
            this.zaznaczone = true;
            for (Pole c : polaSasiadujace) {
                c.setNiezaznaczone();
            }
        }
    }

    void setUtworzenieCzarnych() throws juzZaznaczoneException, Solver.NiepoprawnyStanException {
        if (this.zaznaczone) {
            return;
        }
        if (this.niezaznaczone) {
            throw new juzZaznaczoneException();
        } else {
            this.zaznaczone = true;
            if (!stanGry.czyPolaczone()) throw new Solver.NiepoprawnyStanException();
            for (Pole c : polaSasiadujace) {
                c.setUtworzenieBialych();
            }
        }
    }

    boolean czyNiezaznaczone() {
        return niezaznaczone;
    }

    private boolean czySprawdzone() {
        return sprawdzone;
    }

    void setSprawdzone(boolean sprawdzone) {
        this.sprawdzone = sprawdzone;
    }

    void setNiezaznaczone() throws juzZaznaczoneException {
        if (this.niezaznaczone) return;
        if (this.zaznaczone) throw new juzZaznaczoneException();
        else {
            this.niezaznaczone = true;
            for (Pole c : this.getRzad()) {
                if (c.getWartosc() == this.wartosc && !c.equals(this)) c.setZaznaczone();
            }
            for (Pole c : this.getKolumna()) {
                if (c.getWartosc() == this.wartosc && !c.equals(this)) c.setZaznaczone();
            }
        }
    }


    void setUtworzenieBialych() throws juzZaznaczoneException {
        if (this.niezaznaczone) {
            return;
        }
        if (this.zaznaczone) throw new juzZaznaczoneException();
        else {
            this.niezaznaczone = true;

            if (this.wartosc > 0) { // I have already created black pattern
                for (Pole c : this.getRzad()) {
                    if (c.getWartosc() == this.wartosc && !c.equals(this)) {
                        c.setZaznaczone();
                    }

                    c.mozliweWartosci.remove(Integer.valueOf(this.wartosc));
                }
                for (Pole c : this.getKolumna()) {
                    if (c.getWartosc() == this.wartosc && !c.equals(this)) c.setZaznaczone();

                    c.mozliweWartosci.remove(Integer.valueOf(this.wartosc));
                }
            }
        }
    }

    Pole[] getRzad() {
        return this.stanGry.getPlansza()[this.x];
    }

    Pole[] getKolumna() {
        Pole[] kolumna = new Pole[stanGry.getRozmiar()];
        for (int i = 0; i < stanGry.getRozmiar(); i++) {
            kolumna[i] = stanGry.getPlansza()[i][this.y];
        }
        return kolumna;
    }

    void setPolaSasiadujace() {
        if (polaSasiadujace.isEmpty()) {
            if (x != 0) polaSasiadujace.add(stanGry.getPlansza()[x - 1][y]);
            if (x != stanGry.getRozmiar() - 1) polaSasiadujace.add(stanGry.getPlansza()[x + 1][y]);
            if (y != 0) polaSasiadujace.add(stanGry.getPlansza()[x][y - 1]);
            if (y != stanGry.getRozmiar() - 1) polaSasiadujace.add(stanGry.getPlansza()[x][y + 1]);
        }
    }

    Set<Pole> getPolaSasiadujace() {
        return polaSasiadujace;
    }

    Set<Pole> getOsiagalnePola() {
        Set<Pole> osiagalnePola = new HashSet<>();
        this.setSprawdzone(true);

        if (zaznaczone) return osiagalnePola;

        osiagalnePola.add(this);

        for (Pole c : polaSasiadujace) {
            if (c.czySprawdzone())
                continue;
            if (!c.czyZaznaczone()) {
                osiagalnePola.addAll(c.getOsiagalnePola());
            }
        }

        return osiagalnePola;
    }

    Solver setCzarnePobierzStan() throws Solver.NiepoprawnyStanException {
        if (polaSasiadujace.stream().anyMatch(Pole::czyZaznaczone)) throw new Solver.NiepoprawnyStanException();

        try {
            Solver g = new Solver(this.stanGry);
            g.getPlansza()[x][y].setZaznaczone();

            if (!g.czyPolaczone()) throw new Solver.NiepoprawnyStanException();

            return g;
        } catch (juzZaznaczoneException e) {
            throw new Solver.NiepoprawnyStanException();
        }
    }

    @Override
    public String toString() {
        return zaznaczone ? "\u25A0" : String.valueOf(wartosc);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pole)) {
            return false;
        }
        Pole c2 = (Pole) obj;
        if (this.wartosc != c2.wartosc) return false;
        if (this.x != c2.x) return false;
        if (this.y != c2.y) return false;
        if (this.zaznaczone != c2.zaznaczone) return false;
        return true;
        //return this.x == c2.x && this.y == c2.y && (this.zaznaczone == c2.zaznaczone || (this.wartosc == c2.wartosc && this.niezaznaczone == c2.niezaznaczone));
    }

    void przywrocStanPola() {
        this.zaznaczone = false;
        this.niezaznaczone = false;
    }

    void setWartosc(int wartosc) {
        this.wartosc = wartosc;
    }

    void setWartoscOrazOdznaczPole(Integer n) throws juzZaznaczoneException {
        this.setWartosc(n);
        this.setUtworzenieBialych();
    }

    List<Solver> ustawMozliweDoPrzypisaniaWartosciPobierzStan() {
        List<Solver> gry = new ArrayList<>();
        for (Integer k : mozliweWartosci) {
            Solver g = new Solver(this.stanGry, true);
            Pole p = g.getPlansza()[x][y];
            try {
                p.setWartoscOrazOdznaczPole(k);
            } catch (juzZaznaczoneException e) {
                continue;
            }
            gry.add(g);
        }
        Collections.shuffle(gry);
        return gry;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public List<Integer> getMozliweWartosci() {
        return mozliweWartosci;
    }

    class juzZaznaczoneException extends Throwable {
    }
}