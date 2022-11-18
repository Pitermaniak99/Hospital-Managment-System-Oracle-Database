import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serial;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    JFormattedTextField textField = new JFormattedTextField(DateFormat.getDateInstance(DateFormat.SHORT));
    String appointmentDate, spec;
    int doctorID, appointmentID = 2;
    JLabel specText, doctorText, dateText, panelDescription;
    JButton backButton, addButton;
    JComboBox specBox, doctorsBox = new JComboBox();
    ArrayList<String> specList = new ArrayList<>();
    ArrayList<Integer> doctorsIDList = new ArrayList<>();
    Patient activePatient;
    Doctor activeDoctor;

    Appointment(int patientID, String patientName) {
        activePatient = new Patient(patientID, patientName);
    }

    Appointment(String doctorName, int doctorID) {
        activeDoctor = new Doctor(doctorName, doctorID);
    }

    void calendar() {
        CalendarWindow calendarWindow = new CalendarWindow();

        java.util.Date actualDate = new java.util.Date();
        textField.setValue(actualDate);
        textField.setBounds(180, 400, 80, 30);

        calendarWindow.addPropertyChangeListener(this);
        JButton calendarButton = new JButton("Kalendarz");
        calendarButton.setBounds(280, 400, 120, 30);

        calendarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calendarWindow.setLocation(textField.getLocationOnScreen().x, (textField.getLocationOnScreen().y + textField.getHeight()));
                calendarWindow.resetSelection((Date) textField.getValue());
                calendarWindow.setUndecorated(true);
                calendarWindow.setVisible(true);
            }
        });
        add(textField);
        add(calendarButton);
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

    String dateFormatChange(Date fullDate){
        String pattern = "dd.MM.yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(fullDate);
    }

    void selectingDoctorsSpec() {
        try {
            Connection con = DriverManager.getConnection(dbURL, username, password);
            String sqlSpec = "select SPEC from DOCTORS";
            PreparedStatement specStatement = con.prepareStatement(sqlSpec);
            ResultSet specResultSet = specStatement.executeQuery();
            Set<String> specHashSet = new HashSet<>();
            while (specResultSet.next()) {specHashSet.add(specResultSet.getString("SPEC"));}

            specList.addAll(specHashSet);
            if (!specList.isEmpty()) {spec = specList.get(0);}

            con.close();
        } catch (SQLException e) {
            System.out.println("Błąd podczas pobierania specjalizacji lekarzy:");
            throw new RuntimeException(e);}
    }
    void selectingDoctorsWithSpec(DefaultComboBoxModel model) {
        try {
            Connection con = DriverManager.getConnection(dbURL, username, password);
            String sqlDoctors = "select DOCTOR_ID, USER_NAME from DOCTORS where SPEC='" + spec + "'";
            PreparedStatement doctorsStatement = con.prepareStatement(sqlDoctors);
            ResultSet doctorsSet = doctorsStatement.executeQuery();
            model.removeAllElements();
            while (doctorsSet.next()) {
                model.addElement(doctorsSet.getString("USER_NAME"));
                doctorsIDList.add(doctorsSet.getInt("DOCTOR_ID"));
            }
            doctorsBox.setModel(model);
            doctorID = doctorsIDList.get(0);
            con.close();
        } catch (SQLException f) {
            System.out.println("Błąd podczas pobierania imion lekarzy:");
            throw new RuntimeException(f);}
    }

    int setAppoitmentID() {
        try {
            Connection con = DriverManager.getConnection(dbURL, username, password);
            String sqlAppID = "select APPOINTMENT_ID from  APPOINTMENTS";
            PreparedStatement AppIDStatement = con.prepareStatement(sqlAppID);
            ResultSet AppIDSet = AppIDStatement.executeQuery();
            while (AppIDSet.next()) { appointmentID = AppIDSet.getInt("APPOINTMENT_ID"); }
            con.close();
            return appointmentID + 1;
        } catch (SQLException f) {
            System.out.println("Błąd podczas pobierania ID spotkania:");
            throw new RuntimeException(f);}
    }

    int setDoctorID(String doctorName) {
        try {
            Connection con = DriverManager.getConnection(dbURL, username, password);
            String sqlDoctorID = "select DOCTOR_ID from DOCTORS where USER_NAME='" + doctorName + "'";
            PreparedStatement doctorIDStatement = con.prepareStatement(sqlDoctorID);
            ResultSet doctorIDSet = doctorIDStatement.executeQuery();
            con.close();
            return doctorIDSet.getInt("DOCTOR_ID");

        } catch (SQLException f) {
            System.out.println("Błąd podczas pobierania ID lekarza:");
            throw new RuntimeException(f);}
    }

    void insertNewAppointment(){
        try {
            Connection con = DriverManager.getConnection(dbURL, username, password);
            String sqlApp = "insert into APPOINTMENTS (APPOINTMENT_ID, APPOINTMENT_DATE, DOCTOR_ID, PATIENT_ID) " +
                    "values ('"+appointmentID+"', to_date('"+appointmentDate+"', 'dd.mm.yyyy'), '"+doctorID+"', '"+activePatient.patientID+"')";
            PreparedStatement appStatement = con.prepareStatement(sqlApp);
            appStatement.executeQuery();
            System.out.println("Dodane nowe spotkanie");
            con.close();
        } catch (SQLException f) {
            System.out.println("Błąd podczas dodawania nowego spotkania:");
            throw new RuntimeException(f);}
    }

    void createAppointment() {
        JPanel template = activePatient.patientPanelTemplate();
        add(template);
        calendar();
        selectingDoctorsSpec();

        DefaultComboBoxModel model = (DefaultComboBoxModel) doctorsBox.getModel();
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
                    selectingDoctorsWithSpec(model);
                }}});
        doctorsBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == specBox) {
                    doctorID = setDoctorID((String) doctorsBox.getSelectedItem());
                }}});

        doctorsBox.setBounds(180, 300, 150, 30);
        add(doctorsBox);
        doctorText = new JLabel("Specjalista:");
        doctorText.setFont(infoFont);
        doctorText.setBounds(50, 300, 120, 30);
        add(doctorText);

        dateText = new JLabel("Data wizyty:");
        dateText.setFont(infoFont);
        dateText.setBounds(50, 400, 120, 30);
        add(dateText);

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

        addButton = new JButton("Zapisz zmiany");
        addButton.setBounds(350, 600, 130, 30);
        add(addButton);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!doctorsIDList.isEmpty()) {
                    appointmentDate = dateFormatChange((Date) textField.getValue());
                    appointmentID = setAppoitmentID();
                    insertNewAppointment();
                    setVisible(false);
                    try {
                        new Patient(activePatient.patientID);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                else {JOptionPane.showMessageDialog(null, "Wybierz specjalizację!");}
            }});
        setLayout(null);
        setVisible(true);
        setSize(650,700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }



}