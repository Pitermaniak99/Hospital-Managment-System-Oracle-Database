import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

class Report extends JFrame{
    String dbURL = "jdbc:oracle:thin:@DESKTOP-7JS83K2:1522:xe";
    String username = "system";
    String password = "admin";

    int reportID, appointmentID, doctorID = 1, patientID, medicineID;
    String doctorName, patientName, medicineName;

    Report(int id) throws SQLException {
        appointmentID = id;
        doctorName = getDoctorName(doctorID);
        reportPanelHome();
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

    JPanel reportPanelTemplate(){
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

    void reportPanelHome() {
        JFrame homeFrame = new JFrame();
        JPanel template = reportPanelTemplate();
        homeFrame.add(template);

        JLabel myVisits = new JLabel("Recepta dla pacjenta         z dnia    ");
        myVisits.setFont(new Font("Serif",Font.BOLD,22));
        myVisits.setBounds(10, 95, 600, 50);
        homeFrame.add(myVisits);

        JButton backButton=new JButton("Wróć");
        backButton.setBounds(500,600,100,30);
        homeFrame.add(backButton);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                homeFrame.setVisible(false);
                try {
                    new Doctor(doctorID);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        homeFrame.setLayout(null);
        homeFrame.setVisible(true);
        homeFrame.setSize(650,700);
        homeFrame.setLocationRelativeTo(null);
        homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    public static void main(String[] args) throws SQLException {
        Report newReport;
        newReport = new Report(1);
    }

}
