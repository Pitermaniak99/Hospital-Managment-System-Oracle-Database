import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

class Patient extends JFrame{
    String dbURL = "jdbc:oracle:thin:@DESKTOP-7JS83K2:1522:xe";
    String username = "system";
    String password = "admin";
    JLabel title, hello;
    JButton loguot, newAppointment, myAppointments, myReportsAndBills;
    int patientID;
    String patientName;
    ResultSet patientData;

    Patient(int id) throws SQLException {
        patientID = id;
        try {
            Connection con = DriverManager.getConnection(dbURL, username, password);
            String sql = "select * from PATIENTS where patient_id='" + patientID + "'";
            PreparedStatement ps = con.prepareStatement(sql);
            patientData = ps.executeQuery();
            patientData.next();
            patientName = patientData.getString("USER_NAME");
            con.close();
            patientPanelHome();
        } catch (SQLException e) {
            System.out.println("Błąd podczas tworzenie obiektu pacienta:");
            throw new RuntimeException(e);
        }
    }

    Patient(int id, String name){
        patientID = id;
        patientName = name;
    }

    public void patientPanelTemplate(){
        Font panelFont=new Font("Arial",Font.BOLD,22);
        title=new JLabel("PANEL PACJENTA");
        title.setFont(panelFont);
        title.setBounds(20,10,350,50);
        add(title);

        Font helloFont=new Font("Arial",Font.BOLD,20);
        hello=new JLabel("Witaj "+this.patientName);
        hello.setFont(helloFont);
        hello.setBounds(20,60,350,50);
        add(hello);

        setLayout(null);
        setVisible(true);
        setSize(650,700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    void patientPanelHome() throws SQLException {
        patientPanelTemplate();

        newAppointment=new JButton("Umów nową wizytę");
        newAppointment.setBounds(200,150,250,40);
        add(newAppointment);

        myAppointments=new JButton("Umówione wizyty");
        myAppointments.setBounds(200,220,250,40);
        add(myAppointments);

        myReportsAndBills=new JButton("Wyniki i recepty");
        myReportsAndBills.setBounds(200,290,250,40);
        add(myReportsAndBills);

        loguot=new JButton("Wyloguj");
        loguot.setBounds(500,600,100,30);
        add(loguot);
        loguot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                new Login();
            }
        });

        newAppointment.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                Appointment createApp;
                createApp = new Appointment(patientID, patientName);
                createApp.createAppointment();
            }
        });
    }


    public static void main(String[] args) {
        Appointment createApp;
        createApp = new Appointment(1, "Błażej Ostrowski");
        createApp.createAppointment();
    }

}
