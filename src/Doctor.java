import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

class Doctor extends JFrame{
    String dbURL = "jdbc:oracle:thin:@DESKTOP-7JS83K2:1522:xe";
    String username = "system";
    String password = "admin";

    int doctorID, appointmentsNumber;
    String doctorName;
    ArrayList<String> patientNames = new ArrayList<>(), appointmentDates = new ArrayList<>();
    ArrayList<String> appointmentIDs = new ArrayList<>();

    Doctor(int id) throws SQLException {
        doctorID = id;
        doctorName = getDoctorName(doctorID);
        doctorPanelHome();
    }
    Doctor(String name, int id){
        doctorID = id;
        doctorName = name;
    }

    String getDoctorName(int id){
        String name;
        try {
            Connection con = DriverManager.getConnection(dbURL, username, password);
            String sql = "select USER_NAME from DOCTORS where doctor_id='" + id + "'";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet doctorData;
            doctorData = ps.executeQuery();
            doctorData.next();
            name = doctorData.getString("USER_NAME");
            con.close();
        } catch (SQLException e) {
            System.out.println("Błąd podczas pobieranie nazwy lekarza");
            throw new RuntimeException(e);
        }
        return name;
    }

    JPanel doctorPanelTemplate(){
        JPanel template = new JPanel();
        JLabel title = new JLabel("                         PANEL LEKARZA                        ");
        title.setFont(new Font("Arial",Font.ITALIC,30));
        template.add(title);

        JLabel hello = new JLabel("dr "+this.doctorName);
        hello.setFont(new Font("Arial",Font.BOLD,28));
        template.add(hello);
        template.setBackground(Color.cyan);
        template.setSize(650,100);

        return template;
    }

    void doctorPanelHome() {
        JFrame homeFrame = new JFrame();
        JPanel template = doctorPanelTemplate();
        homeFrame.add(template);

        JLabel myVisits = new JLabel("Nadchodzące wizyty: ");
        myVisits.setFont(new Font("Serif",Font.BOLD,26));
        myVisits.setBounds(20, 120, 250, 50);
        homeFrame.add(myVisits);

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

        String[][] data = createAppointmentsArray();
        String[] columns ={"Pacjent","Data wizyty","Wystaw receptę"};
        int tableHeight = appointmentsNumber * 26 +34;
        int tableWidth = 500;

        ButtonColumn bc = new ButtonColumn(homeFrame, data, columns, tableWidth, tableHeight);
        JScrollPane sp = bc.getPane();


        JPanel tablePanel = new JPanel();
        tablePanel.add(sp);
        tablePanel.setSize(tableWidth, tableHeight);
        tablePanel.setLocation(75, 200);
        homeFrame.add(tablePanel);

        homeFrame.setLayout(null);
        homeFrame.setVisible(true);
        homeFrame.setSize(650,700);
        homeFrame.setLocationRelativeTo(null);
        homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    void getMyAppointments() {
        try {
            Connection con = DriverManager.getConnection(dbURL, username, password);
            String sqlMyApp = "select APPOINTMENT_ID, APPOINTMENT_DATE, PATIENT_ID from APPOINTMENTS where DOCTOR_ID='" + doctorID + "'";
            PreparedStatement myAppStatement = con.prepareStatement(sqlMyApp);
            ResultSet myAppSet = myAppStatement.executeQuery();
            ArrayList<Integer> patientIDs = new ArrayList<>();
            appointmentDates.clear();
            appointmentIDs.clear();
            patientNames.clear();
            while (myAppSet.next()){
                appointmentDates.add(myAppSet.getString("APPOINTMENT_DATE").replace("00:00:00", ""));
                appointmentIDs.add(myAppSet.getString("APPOINTMENT_ID"));
                patientIDs.add(myAppSet.getInt("PATIENT_ID"));
            }
            int i = 0;
            while (i<patientIDs.size()) {
                String sqlPatientsData = "select USER_NAME from PATIENTS where PATIENT_ID='" + patientIDs.get(i) + "'";
                PreparedStatement myPatientsStatement = con.prepareStatement(sqlPatientsData);
                ResultSet myPatientsSet = myPatientsStatement.executeQuery();
                while (myPatientsSet.next()){
                    patientNames.add(myPatientsSet.getString("USER_NAME"));
                }
                i++;
            }
            appointmentsNumber = patientNames.size();
            System.out.println("Pobrano dane wizyt");
            con.close();
        } catch (SQLException f) {
            System.out.println("Błąd podczas pobierania listy wizyt:");
            throw new RuntimeException(f);}
    }

    String[][] createAppointmentsArray(){
        getMyAppointments();
        int columns = 3;
        int rows = appointmentsNumber;
        String[][] patientsData = new String[rows][columns];
        for (int i = 0; i<rows; i++){
            patientsData[i][0] = patientNames.get(i);
        }
        for (int i = 0; i<rows; i++){
            patientsData[i][1] = appointmentDates.get(i);
        }
        for (int i = 0; i<rows; i++){
            patientsData[i][2] = "ID wizyty: "+appointmentIDs.get(i);
        }
        return patientsData;
    }

    public static void main(String[] args) throws SQLException {
        Doctor newDoctor;
        newDoctor = new Doctor(1);
    }

}
