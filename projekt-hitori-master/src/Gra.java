import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Stack;

class Gra {
    static ObslugaPliku obslugaPliku = new ObslugaPliku();
    private static Solver g;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Gra Hitori");
        frame.setBounds(100, 100, 650, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        

        JLabel witajLabel = new JLabel("Witaj w grze Hitori!");
        witajLabel.setFont(new Font("Consolas", Font.BOLD, 18));
        witajLabel.setBounds(70, 50, 600, 50);
        frame.getContentPane().add(witajLabel);

        JLabel chceszLabel = new JLabel("Chcesz wygenerować planszę czy wczytać ją z pliku?");
        chceszLabel.setFont(new Font("Consolas", Font.PLAIN, 16));
        chceszLabel.setBounds(70, 150, 600, 50);
        frame.getContentPane().add(chceszLabel);

        JLabel rozmiarLabel = new JLabel("Jaki poziom trudności (rozmiar)?");
        rozmiarLabel.setFont(new Font("Consolas", Font.PLAIN, 14));
        rozmiarLabel.setBounds(70, 300, 600, 24);
        frame.getContentPane().add(rozmiarLabel);


        JSlider rozmiarSlider = new JSlider(JSlider.HORIZONTAL, 2, 9, 5);
        rozmiarSlider.setMinorTickSpacing(1);
        rozmiarSlider.setBounds(70,340,220,30);
        rozmiarSlider.setPaintTicks(true);
        rozmiarSlider.createStandardLabels(1);
        frame.getContentPane().add(rozmiarSlider);

        JButton wygeneruj = new JButton("Wygeneruj planszę");
        wygeneruj.setBounds(70,450,220,30);
        frame.getContentPane().add(wygeneruj);
        wygeneruj.addActionListener(actionEvent -> {
            g = new Solver(rozmiarSlider.getValue());
            gramy(frame,g);
        });

        JButton wczytaj = new JButton("Wczytaj z pliku");
        wczytaj.setBounds(360,450,200,30);
        wczytaj.addActionListener(actionEvent -> {
            do {
                JFileChooser fc = new JFileChooser();
                fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Plansze Hitori", "hitori");
                fc.setFileFilter(filter);
                fc.showOpenDialog(frame);
                File plik = fc.getSelectedFile();
                Integer[][] liczby;
                try {
                    liczby = obslugaPliku.wczytajPlik(plik);
                    g = new Solver(liczby);
                    gramy(frame,g);
                    break;
                } catch (Exception | Pole.juzZaznaczoneException e) {
                    e.printStackTrace();
                }
            } while (true);
        });
        frame.getContentPane().add(wczytaj);

        frame.setVisible(true);

        System.out.println("Witaj w grze Hitori! Chcesz wygenerować planszę (1) czy wczytać ją z pliku (2)?");
        Scanner s = new Scanner(System.in);
        String ans1;

        do {
            ans1 = s.nextLine();
            if (ans1.equals("1") || ans1.equals("2")) break;
            else System.out.println("Chcesz wygenerować planszę (1) czy wczytać ją z pliku (2)?");
        } while (true);
        int rozmiar;
        if (ans1.equals("1")) { //generowanie planszy
            System.out.println("Jaki poziom trudności (rozmiar)? 5/7/9");
            String ans2;
            do {
                ans2 = s.nextLine();
                try {
                    rozmiar = Integer.parseInt(ans2);
                    if (rozmiar < 2) throw new Exception();
                    g = new Solver(rozmiar);
                    break;
                } catch (Exception e) {
                    System.out.println("Jaki poziom trudności (rozmiar)? 5/7/9");
                }
            } while (true);
        } else {
            System.out.println("Podaj nazwę pliku, z którego chcesz wczytać planszę: ");
            String nazwaPliku;
            do {
                try {
                    nazwaPliku = s.nextLine();
                    if (!nazwaPliku.endsWith(".hitori")) throw new NoSuchElementException();
                    File plik = new File(nazwaPliku);
                    Integer[][] liczby = obslugaPliku.wczytajPlik(plik);
                    g = new Solver(liczby);
                    break;
                } catch (FileNotFoundException e) {
                    System.out.println("Nie znaleziono pliku. Podaj nazwę pliku, z którego chcesz wczytać planszę:");
                } catch (NoSuchElementException e) {
                    System.out.println("Nieprawidlowy typ pliku. Podaj nazwę pliku z rozszerzeniem '.hitori':");
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Nieprawidlowa struktura pliku. Podaj nazwę poprawnego pliku: ");
                } catch (Pole.juzZaznaczoneException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
        Solver originalGame = new Solver(g);
        Solver czysta = Solver.wyczysc(g);
        Solver solution = BST(czysta);
        if (solution == null) {
            System.out.println("Plansza nie ma rozwiązania.\n");
        }
        String[] splited;
        int x, y;
        label:
        do {
            try {
                System.out.println("Plansza:\n" + originalGame.toString());
                System.out.println("Podaj wspolrzedne (x y) pola do zmiany, s aby zapisać i wyjść, p aby zapisać do wydruku, a aby wyświetlić podpowiedź lub r aby sprawdzić rozwiązanie");
                ans1 = s.nextLine();
                switch (ans1) {
                    case "s":
                        obslugaPliku.zapiszPlik(originalGame.getPlansza());
                        System.out.println("Zapisano do pliku");
                        System.exit(0);
                    case "p":
                        obslugaPliku.zapiszWydruk(originalGame.getPlansza());
                        System.out.println("Zapisano do wydruku");
                        break;
                    case "a":
                        if(!originalGame.czyRozwiazane()) {
                            Solver.podpowiedz(originalGame.getPlansza(), solution.getPlansza(),null);
                        }
                        else{
                            System.out.println("Gratulacje! Brak podpowiedzi, gra została rozwiązana");
                            break label;
                        }
                        break;
                    case "r":
                        if (originalGame.czyZaznaczonePolaPolaczoneWrzedach() || originalGame.czyZaznaczonePolePolaczoneWkolumnach()) {
                            System.out.println("Zaznaczone pola nie mogą być połączone bokami!");
                        }
                        if (!originalGame.czyPolaczone()) {
                            System.out.println("Niezaznaczone pola nie tworzą ciągłej sieci");
                        }
                        if (originalGame.powtorzeniaWkolumnach() || originalGame.powtorzeniaWrzedach()) {
                            System.out.println("Wartości nie mogą się powtarzać w rzędach ani kolumnach!");
                        } else {
                            System.out.println("Brawo! Gra rozwiązana!");
                            //System.out.println("Rozwiązanie:\n" + solution.toString());
                            break label;
                        }
                        break;
                    default:
                        splited = ans1.split(" ", 2);
                        x = Integer.parseInt(splited[0]) - 1;
                        y = Integer.parseInt(splited[1]) - 1;
                        originalGame.zmienPole(y, x);
                        break;
                }
            } catch (Exception e) {
                System.out.println("Nie podano prawidlowych argumentow");
            }
        } while (true);
    }

    static void gramy(JFrame frame, Solver g){
        Solver originalGame = new Solver(g);
        Solver czysta = Solver.wyczysc(g);
        Solver solution = BST(czysta);

        frame.getContentPane().removeAll();
        frame.repaint();

        // TODO: 14.06.2020 pętla for z kwadratowymi buttonami i listenerami dla nich
        for (int i = 0; i < g.rozmiar; i++) {
            for (int j = 0; j < g.rozmiar; j++) {
                JButton button = new JButton(String.valueOf(g.plansza[i][j].wartosc));
                button.setBounds(70+j*50,40+i*50,45,45);
                if (g.plansza[i][j].zaznaczone) button.setBackground(Color.LIGHT_GRAY);
                else button.setBackground(Color.WHITE);
                int finalI = i;
                int finalJ = j;
                button.addActionListener(actionEvent -> {
                    originalGame.zmienPole(finalI, finalJ);
                    if (originalGame.plansza[finalI][finalJ].zaznaczone) button.setBackground(Color.LIGHT_GRAY);
                    else button.setBackground(Color.WHITE);
                });
                frame.getContentPane().add(button);
            }
        }

        JButton sprawdz = new JButton("Sprawdź");
        sprawdz.setBounds(70,500,90,30);
        frame.getContentPane().add(sprawdz);

        JButton podpowiedz = new JButton("Podpowiedź");
        podpowiedz.setBounds(170,500,110,30);
        frame.getContentPane().add(podpowiedz);

        JButton zapiszPlik = new JButton("Zapisz do pliku");
        zapiszPlik.setBounds(290,500,120,30);
        frame.getContentPane().add(zapiszPlik);

        JButton zapiszWydruk = new JButton("Zapisz do wydruku");
        zapiszWydruk.setBounds(420,500,140,30);
        frame.getContentPane().add(zapiszWydruk);

        JLabel info = new JLabel(" ");
        info.setBounds(70,10,500,30);
        frame.getContentPane().add(info);
        info.setText("Plansza:");

        sprawdz.addActionListener(actionEvent -> {
            if (originalGame.czyZaznaczonePolaPolaczoneWrzedach() || originalGame.czyZaznaczonePolePolaczoneWkolumnach()) {
                info.setText("Zaznaczone pola nie mogą być połączone bokami!");
            }
            if (!originalGame.czyPolaczone()) {
                info.setText("Niezaznaczone pola nie tworzą ciągłej sieci");
            }
            if (originalGame.powtorzeniaWkolumnach() || originalGame.powtorzeniaWrzedach()) {
                info.setText("Wartości nie mogą się powtarzać w rzędach ani kolumnach!");
            } else {
                info.setText("Brawo! Gra rozwiązana!");
            }
        });

        podpowiedz.addActionListener(actionEvent -> {
            if(!originalGame.czyRozwiazane()) {
                assert solution != null;
                Solver.podpowiedz(originalGame.getPlansza(), solution.getPlansza(), info);
            }
            else{
                info.setText("Gratulacje! Brak podpowiedzi, gra została rozwiązana");
            }
        });

        zapiszPlik.addActionListener(actionEvent -> {
            try {
                obslugaPliku.zapiszPlik(originalGame.getPlansza());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            info.setText("Zapisano do pliku");
        });

        zapiszWydruk.addActionListener(actionEvent -> {
            try {
                obslugaPliku.zapiszWydruk(originalGame.getPlansza());
            } catch (IOException e) {
                e.printStackTrace();
            }
            info.setText("Zapisano do wydruku");
        });

        if (solution == null) {
            info.setText("Plansza nie ma rozwiązania.");
            info.setForeground(Color.RED);
            podpowiedz.setEnabled(false);
        }
        /*
        int x = 0, y = 0;
        do {
                System.out.println("Plansza:\n" + originalGame.toString());
                originalGame.zmienPole(y, x);
        } while (true);
         */
    }

    private static Solver BST(Solver stan) {

        Stack<Solver> stos = new Stack<>();
        stos.push(stan);
        while (stos.size() != 0) {
            Solver element = stos.pop();
            try {
                element.wnioskowanie();
            } catch (Solver.NiepoprawnyStanException e) {
                continue;
            }
            if (element.czyRozwiazane()) {
                return element;
            }
            for (Solver g : element.getOpcjeNastepnegoKroku()) {
                stos.push(g);
            }
        }
        return null;
    }
}