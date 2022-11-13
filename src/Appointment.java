import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serial;
import java.sql.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

class Appointment extends JFrame implements PropertyChangeListener {
    String dbURL = "jdbc:oracle:thin:@DESKTOP-7JS83K2:1522:xe";
    String username = "system";
    String password = "admin";
    @Serial
    private static final long serialVersionUID = 1L;
    //the TextField for typing the date
    JFormattedTextField textField = new JFormattedTextField(DateFormat.getDateInstance(DateFormat.SHORT));
    int doctorID, patientID, appointmentID;
    String spec;
    JLabel specText, doctorText, dateText, panelDescription;
    JButton backButton, addButton;
    JComboBox specBox;
    JComboBox doctorsBox = new JComboBox();

    Set<String> specList = new HashSet<>();
    ArrayList<String> doctorsList = new ArrayList<>();
    Patient activePatient;
    Doctor activeDoctor;

    Appointment(int patientID, String patientName) {
        activePatient = new Patient(patientID, patientName);
    }

    Appointment(String doctorName, int doctorID) {
        activeDoctor = new Doctor(doctorName, doctorID);
    }

    void patientPanelTemplate() {
        Font panelFont = new Font("Arial", Font.BOLD, 22);
        activePatient.title = new JLabel("PANEL PACJENTA");
        activePatient.title.setFont(panelFont);
        activePatient.title.setBounds(20, 10, 350, 50);
        add(activePatient.title);

        Font helloFont = new Font("Arial", Font.BOLD, 20);
        activePatient.hello = new JLabel("Witaj " + activePatient.patientName);
        activePatient.hello.setFont(helloFont);
        activePatient.hello.setBounds(20, 60, 350, 50);
        add(activePatient.hello);

        backButton = new JButton("Anuluj");
        backButton.setBounds(500, 600, 100, 30);
        add(backButton);


        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                try {
                    new Patient(activePatient.patientID);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        setLayout(null);
        setVisible(true);
        setSize(650, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    void calendar() {
        CalendarWindow calendarWindow = new CalendarWindow();

        textField.setValue(new java.util.Date());
        textField.setBounds(180, 400, 80, 30);

        //wire a listener for the PropertyChange event of the calendar window
        calendarWindow.addPropertyChangeListener(this);
        JButton calendarButton = new JButton("Kalendarz");
        calendarButton.setBounds(280, 400, 120, 30);

        calendarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //render the calendar window below the text field
                calendarWindow.setLocation(textField.getLocationOnScreen().x, (textField.getLocationOnScreen().y + textField.getHeight()));
                //get the Date and assign it to the calendar
                java.util.Date d = (Date) textField.getValue();

                calendarWindow.resetSelection(d);
                calendarWindow.setUndecorated(true);
                calendarWindow.setVisible(true);
            }
        });
        add(textField);
        add(calendarButton);
    }

    void createAppointment() {
        patientPanelTemplate();
        calendar();
        DefaultComboBoxModel model = (DefaultComboBoxModel) doctorsBox.getModel();
        try {
            Connection con = DriverManager.getConnection(dbURL, username, password);
            System.out.println("Connected to Oracle database[DOCTORS]");
            String sqlSpec = "select SPEC from DOCTORS";
            PreparedStatement specStatement = con.prepareStatement(sqlSpec);
            ResultSet specSet = specStatement.executeQuery();
            while (specSet.next()){
                specList.add(specSet.getString("SPEC"));
            }
            String[] specArray = specList.toArray(new String[specList.size()]);
            if(!specList.isEmpty()) {
                spec = specArray[0];
            }
            con.close();
        } catch (SQLException e) {
            System.out.println("Błąd podczas pobierania danych lekarzy:");
            throw new RuntimeException(e);
        }
        Font descriptionFont = new Font("Arial", Font.BOLD, 18);
        panelDescription = new JLabel("Wybierz specjalistę oraz datę wizyty:");
        panelDescription.setFont(descriptionFont);
        panelDescription.setBounds(50, 120, 400, 60);
        add(panelDescription);

        Font infoFont = new Font("Arial", Font.BOLD, 16);
        specText = new JLabel("Specjalizacja:");
        specText.setFont(infoFont);
        specText.setBounds(50, 200, 150, 30);
        add(specText);

        specBox = new JComboBox(specList.toArray());
        specBox.setBounds(180, 200, 120, 30);
        add(specBox);

        specBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == specBox) {
                    spec = (String) specBox.getSelectedItem();
                    try {
                        Connection con = DriverManager.getConnection(dbURL, username, password);
                        System.out.println("Connected to Oracle database[DOCTORS]");
                        String sqlDoctors = "select USER_NAME from DOCTORS where SPEC='" + spec + "'";
                        PreparedStatement doctorsStatement = con.prepareStatement(sqlDoctors);
                        ResultSet doctorsSet = doctorsStatement.executeQuery();
                        doctorsList.clear();
                        model.removeAllElements();
                        while (doctorsSet.next()) {
                            doctorsList.add(doctorsSet.getString("USER_NAME"));
                        }
                        for (String item : doctorsList) {
                            model.addElement(item);
                        }
                        doctorsBox.setModel(model);
//                        doctorsBox = new JComboBox(doctorsList.toArray());
                        doctorsBox.setBounds(180, 300, 150, 30);
                        add(doctorsBox);
                        con.close();
                    } catch (SQLException f) {
                        System.out.println("Błąd podczas pobierania danych lekarzy:");
                        throw new RuntimeException(f);
                    }
                }
            }
        });

        doctorText = new JLabel("Specjalista:");
        doctorText.setFont(infoFont);
        doctorText.setBounds(50, 300, 120, 30);
        add(doctorText);

        dateText = new JLabel("Data wizyty:");
        dateText.setFont(infoFont);
        dateText.setBounds(50, 400, 120, 30);
        add(dateText);

        addButton = new JButton("Zapisz zmiany");
        addButton.setBounds(350, 600, 130, 30);
        add(addButton);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                try {
                    new Patient(activePatient.patientID);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {

        //get the selected date from the calendar control and set it to the text field
        if (event.getPropertyName().equals("selectedDate")) {

            java.util.Calendar cal = (java.util.Calendar) event.getNewValue();
            Date selDate = cal.getTime();
            textField.setValue(selDate);
        }

    }

}