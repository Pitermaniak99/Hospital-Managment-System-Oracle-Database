import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

class Patient extends JFrame{
    String dbURL = "jdbc:oracle:thin:@DESKTOP-7JS83K2:1522:xe";
    String username = "system";
    String password = "admin";

    int patientID, appointmentsNumber;
    String patientName;
    ArrayList<String> doctorNames = new ArrayList<>(), doctorSpecs = new ArrayList<>(), appointmentDates = new ArrayList<>();

    Patient(int id) throws SQLException {
        patientID = id;
        patientName = getPatientName(patientID);
        patientPanelHome();
    }

    Patient(int id, String name){
        patientID = id;
        patientName = name;
    }

    String getPatientName(int id) {
        String name;
        try {
            Connection con = DriverManager.getConnection(dbURL, username, password);
            String sql = "select USER_NAME from PATIENTS where patient_id='" + id + "'";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet patientData;
            patientData = ps.executeQuery();
            patientData.next();
            name = patientData.getString("USER_NAME");
            con.close();
        } catch (SQLException e) {
            System.out.println("Błąd podczas pobieranie nazwy pacjenta:");
            throw new RuntimeException(e);
        }
        return name;
    }

    JPanel patientPanelTemplate(){
        JPanel template = new JPanel();
        JLabel title = new JLabel("                         PANEL  PACJENTA                        ");
        title.setFont(new Font("Arial",Font.ITALIC,30));
        template.add(title);

        JLabel hello = new JLabel("Witaj "+this.patientName);
        hello.setFont(new Font("Arial",Font.BOLD,28));
        template.add(hello);
        template.setBackground(Color.cyan);
        template.setSize(650,100);

        return template;
    }

    void patientPanelHome() {
        JFrame homeFrame = new JFrame();
        JPanel template = patientPanelTemplate();
        homeFrame.add(template);

        JButton newAppointment=new JButton("Umów nową wizytę");
        newAppointment.setBounds(200,150,250,40);
        homeFrame.add(newAppointment);

        JButton myAppointments=new JButton("Umówione wizyty");
        myAppointments.setBounds(200,220,250,40);
        homeFrame.add(myAppointments);

        JButton myReportsAndBills=new JButton("Wyniki i recepty");
        myReportsAndBills.setBounds(200,290,250,40);
        homeFrame.add(myReportsAndBills);

        JButton logout=new JButton("Wyloguj");
        logout.setBounds(500,600,100,30);
        homeFrame.add(logout);
        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                homeFrame.setVisible(false);
                new Login();
            }
        });

        newAppointment.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                homeFrame.setVisible(false);
                Appointment createApp;
                createApp = new Appointment(patientID, patientName);
                createApp.createAppointment();
            }
        });

        myAppointments.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                homeFrame.setVisible(false);
                patientPanelMyVisits();
            }
        });

        homeFrame.setLayout(null);
        homeFrame.setVisible(true);
        homeFrame.setSize(650,700);
        homeFrame.setLocationRelativeTo(null);
        homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    String[][] createAppointmentsArray(){
        getMyAppointments();
        int columns = 3;
        int rows = appointmentsNumber;
        String[][] doctorsData = new String[rows][columns];
        for (int i = 0; i<rows; i++){
            doctorsData[i][0] = doctorNames.get(i);
        }
        for (int i = 0; i<rows; i++){
            doctorsData[i][1] = doctorSpecs.get(i);
        }
        for (int i = 0; i<rows; i++){
            doctorsData[i][2] = appointmentDates.get(i);
        }
        return doctorsData;
    }

    void getMyAppointments() {
        try {
            Connection con = DriverManager.getConnection(dbURL, username, password);
            String sqlMyApp = "select APPOINTMENT_DATE, DOCTOR_ID from APPOINTMENTS where PATIENT_ID='" + patientID + "'";
            PreparedStatement myAppStatement = con.prepareStatement(sqlMyApp);
            ResultSet myAppSet = myAppStatement.executeQuery();
            ArrayList<Integer> doctorIDs = new ArrayList<>();
            appointmentDates.clear();
            doctorSpecs.clear();
            doctorNames.clear();
            while (myAppSet.next()){
                appointmentDates.add(myAppSet.getString("APPOINTMENT_DATE").replace("00:00:00", ""));
                doctorIDs.add(myAppSet.getInt("DOCTOR_ID"));
            }
            int i = 0;
            while (i<doctorIDs.size()) {
                String sqlDoctorsData = "select USER_NAME, SPEC from DOCTORS where DOCTOR_ID='" + doctorIDs.get(i) + "'";
                PreparedStatement myDoctorsStatement = con.prepareStatement(sqlDoctorsData);
                ResultSet myDoctorsSet = myDoctorsStatement.executeQuery();
                while (myDoctorsSet.next()){
                    doctorNames.add(myDoctorsSet.getString("USER_NAME"));
                    doctorSpecs.add(myDoctorsSet.getString("SPEC"));
                }
                i++;
            }
            appointmentsNumber = doctorNames.size();
            System.out.println("Pobrano dane wizyt");
            con.close();
        } catch (SQLException f) {
            System.out.println("Błąd podczas pobierania listy wizyt:");
            throw new RuntimeException(f);}
    }

    void patientPanelMyVisits(){
        JFrame visitsFrame = new JFrame();
        JPanel template = patientPanelTemplate();
        visitsFrame.add(template);

        String[][] data = createAppointmentsArray();
        String[] columns ={"Lekarz","Specjalizacja","Data wizyty"};
        JTable appTable = new JTable(data, columns);
        appTable.setRowHeight(26);
        appTable.getTableHeader().setFont(new Font("Serif", Font.BOLD, 18));
        appTable.setFont(new Font("Serif", Font.PLAIN, 18));
        int tableHeight = appointmentsNumber * 26 +34;

        JScrollPane sp = new JScrollPane(appTable);
        JPanel tablePanel = new JPanel();
        tablePanel.add(sp);
        tablePanel.setSize(500, tableHeight);
        tablePanel.setLocation(75, 200);
        visitsFrame.add(tablePanel);

        JButton backButton = new JButton("Wróć");
        backButton.setBounds(500,600,100,30);
        visitsFrame.add(backButton);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                visitsFrame.setVisible(false);
                patientPanelHome();
            }
        });

        visitsFrame.setLayout(null);
        visitsFrame.setVisible(true);
        visitsFrame.setSize(650,700);
        visitsFrame.setLocationRelativeTo(null);
        visitsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) throws SQLException {
        Patient newPatient;
        newPatient = new Patient(2);
    }

}
