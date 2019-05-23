package fullhouse;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.text.MaskFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

public class Gast_Toevoegen extends JDialog {

    //JDIALOG
    private static final int width = 500;
    private static final int height = 500;
    private static final String title = "Gast Toevoegen";

    //DATABASE
    private Connection connection;
    private PreparedStatement ps;

    //ATTRIBUTES
    private String naam;
    private String geslacht = "null";
    private LocalDate geboortedatum;
    private String gbdString;

    private ArrayList<Integer> years;
    private ArrayList<Integer> months;
    private ArrayList<Integer> days;

    private String adres;

    private String postcode;
    private MaskFormatter pformatter;

    private String woonplaats;

    private String telefoonnummer;

    private String email;

    //COMPONENTS
    private JFormattedTextField naamTxt;
    private JRadioButton man;
    private JRadioButton vrouw;
    private JFormattedTextField adresTxt;
    private JFormattedTextField postcodeTxt;
    private JFormattedTextField woonplaatsTxt;
    private JFormattedTextField telefoonnummerTxt;
    private JFormattedTextField emailTxt;

    public static void main(String[] args) {

        JDialog d = new Gast_Toevoegen();
        d.setSize(width, height);
        d.setTitle(title);
        d.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        d.setVisible(true);
    }

    public Gast_Toevoegen() {

            try { //tries to make a connection with the database
                connection = DriverManager.getConnection(
                        "jdbc:mysql://meru.hhs.nl/18064728?useLegacyDatetimeCode=false&serverTimezone=Europe/Amsterdam",
                        "18064728", "aeph3vo3aV");
                System.out.println("connected to database");
            } catch (SQLException e) { //error message if there's no connection with the database
                System.out.println("can't connect to the database, please contact tech-support");
            }

            try {
                pformatter = new MaskFormatter("####??");
            } catch (ParseException e) {
                e.printStackTrace();
            }

                //JPanel
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

                //name
                JLabel naamLbl = new JLabel("naam: ");
                naamTxt = new JFormattedTextField();

                //gender
                ButtonGroup bg = new ButtonGroup();
                man = new JRadioButton("man");
                vrouw = new JRadioButton("vrouw");
                bg.add(man);
                bg.add(vrouw);

                //**date of birth
                JLabel yearLbl = new JLabel("year");
                years = new ArrayList<>(); //number of year

                JLabel monthLbl = new JLabel("month");
                months = new ArrayList<>(); // month of year

                JLabel dayLbl = new JLabel("day");
                days = new ArrayList<>(); // day of month

                getDate(); //gets current date to add to ComboBox
                JComboBox<Object> dayList = new JComboBox<>(days.toArray());
                JComboBox<Object> monthList = new JComboBox<>(months.toArray());
                JComboBox<Object> yearList = new JComboBox<>(years.toArray());
                yearList.setSelectedIndex(119);
                //date of birth**


                //adres
                JLabel adresLbl = new JLabel("adres: ");
                adresTxt = new JFormattedTextField();

                //postcode
                JLabel postcodeLbl = new JLabel("postcode: ");
                postcodeTxt = new JFormattedTextField(pformatter);
                //postcodeTxt.setText("1234ab");
                postcodeTxt.setToolTipText("bv. 1234ab");

                //woonplaats
                JLabel woonplaatsLbl = new JLabel("woonplaats: ");
                woonplaatsTxt = new JFormattedTextField();

                //telefoonnr
                JLabel telefoonnummerLbl = new JLabel("telefoonnummer: ");
                telefoonnummerTxt = new JFormattedTextField();

                //email
                JLabel emailLbl = new JLabel("email: ");
                emailTxt = new JFormattedTextField();

                //adds guest button
                JButton addGuest = new JButton("voeg gast toe");

                //adds components
                panel.add(naamLbl);
                panel.add(naamTxt);

                panel.add(man);
                panel.add(vrouw);

                panel.add(dayLbl);
                panel.add(dayList);
                panel.add(monthLbl);
                panel.add(monthList);
                panel.add(yearLbl);
                panel.add(yearList);

                panel.add(adresLbl);
                panel.add(adresTxt);

                panel.add(postcodeLbl);
                panel.add(postcodeTxt);

                panel.add(woonplaatsLbl);
                panel.add(woonplaatsTxt);

                panel.add(telefoonnummerLbl);
                panel.add(telefoonnummerTxt);

                panel.add(emailLbl);
                panel.add(emailTxt);

                panel.add(Box.createRigidArea(new Dimension(0, 25)));
                panel.add(addGuest);
                add(panel);

        class AddGuest implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                int day;
                int month;
                int year;

                try { //adds guest to database
                    naam = naamTxt.getText();

                    if (man.isSelected()) {
                        geslacht = "man";
                    } else if (vrouw.isSelected()) {
                        geslacht = "vrouw";
                    }

                    day = dayList.getSelectedIndex() + 1;
                    month = monthList.getSelectedIndex() + 1;
                    year = yearList.getSelectedIndex() + 1900;
                    geboortedatum = LocalDate.of(year, month, day);
                    gbdString = year + "-" + month + "-" + day;

                    adres = adresTxt.getText();
                    postcode = postcodeTxt.getText();
                    woonplaats = woonplaatsTxt.getText();
                    telefoonnummer = telefoonnummerTxt.getText();
                    email = emailTxt.getText();

                    if (!is18()) { //checks if user is of legal age
                        throw new underageException();
                    }

                    if (isValidInput()) { //adds guest to the database if the input is valid
                        ps = connection.prepareStatement(
                                "INSERT INTO gast (naam, geslacht, geboortedatum, adres, postcode, woonplaats, telefoonnummer, email) " +
                                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
                        ps.setString(1, naam);
                        ps.setString(2, geslacht);
                        ps.setString(3, gbdString);
                        ps.setString(4, adres);
                        ps.setString(5, postcode);
                        ps.setString(6, woonplaats);
                        ps.setString(7, telefoonnummer);
                        ps.setString(8, email);
                        ps.executeUpdate();
                        System.out.println("Guest is added to the database");
                        dispose();
                    }

                } catch (underageException ex1) {
                    System.out.println("gast is nog geen 18");
                } catch (Exception ex) {
                    System.out.println("\nSomething went wrong");
                    ex.printStackTrace();
                }
            }

        }

        ActionListener listener = new AddGuest();
        addGuest.addActionListener(listener);
    }

    private void getDate() {
        years = new ArrayList<>();
        months = new ArrayList<>();
        days = new ArrayList<>();

        int currentYear = LocalDateTime.now().getYear();

        //year
        for (int i = 1900; i <= currentYear ; i++) {
           years.add(i);
        }

        //month
        for (int i = 1; i <= 12  ; i++) {
            months.add(i);
        }

        //day
        for (int i = 1; i <=31 ; i++) {
            days.add(i);
        }
    }

    private Boolean is18() {
        LocalDate now = LocalDate.now();
        int age = Period.between(geboortedatum, now).getYears();
        return  age >= 18;
    }

    private Boolean isValidInput() { //checks if input is valid
        // boolean validInput = false;

        if (naam.equals("")) {
            System.out.println("vul een naam in a.u.b.");
            naamTxt.setText("vul een naam in a.u.b.");
            return false;
        } if (naam.matches(".*\\d.*")) {
            System.out.println("naam mag geen nummers bevatten");
            naamTxt.setText("naam mag geen nummers bevatten");
            return false;
        } if (geslacht.equals("null")) {
            System.out.println("geen geslacht gekozen");
            return false;
        } if (adres.equals("")) {
            System.out.println("vul een adres in a.u.b.");
            adresTxt.setText("vul een adres in a.u.b.");
            return false;
        } if (postcode.equals("")) {
            System.out.println("vul een postcode in a.u.b.");
            postcodeTxt.setText("vul een postcode in a.u.b.");
        } if (woonplaats.equals("")) {
            System.out.println("vul een woonplaats in a.u.n.");
            woonplaatsTxt.setText("vul een woonplaats in a.u.b.");
            return false;
        } if (telefoonnummer.equals("")) {
            System.out.println("vul een telefoonnummer in a.u.b.");
            telefoonnummerTxt.setText("vul een telefoonnummer in a.u.b.");
            return false;
        } if (email.equals("")) {
            System.out.println("vul een email in a.u.b.");
            emailTxt.setText("vul een email in a.u.b.");
            return false;
        } else {
            return true;
        }
    }

     private class underageException extends Exception {
        private underageException() {
        }
    }
}