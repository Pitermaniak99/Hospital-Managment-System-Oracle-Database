import javax.swing.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

class Doctor extends JFrame{
    String dbURL = "jdbc:oracle:thin:@DESKTOP-7JS83K2:1522:xe";
    String username = "system";
    String password = "admin";
    JLabel title, hello;
    JButton loguot;
    int doctorID;
    String doctorName;
    ResultSet doctorData;
    Doctor(int id) {
        doctorID = id;
        System.out.println(doctorID);

        try {
            Connection con = DriverManager.getConnection(dbURL, username, password);
            System.out.println("Connected to Oracle database");
            String sql = "select * from DOCTORS where doctor_id='" + doctorID + "'";
            PreparedStatement ps = con.prepareStatement(sql);
            doctorData = ps.executeQuery();
            doctorData.next();
            doctorPanel();
            con.close();
        } catch (SQLException e) {
            System.out.println("Błąd podczas łączenia z bazą danych:");
            throw new RuntimeException(e);
        }
    }

    Doctor(String name, int id){
        doctorID = id;
        doctorName = name;
    }

    void doctorPanel() throws SQLException {
        System.out.println(doctorData.getString("USER_NAME"));
        Font panelFont=new Font("Arial",Font.BOLD,26);
        Font helloFont=new Font("Arial",Font.BOLD,20);
        title=new JLabel("Panel lekarza");
        hello=new JLabel("doktor "+doctorData.getString("USER_NAME"));
        loguot=new JButton("Wyloguj");
        title.setFont(panelFont);
        title.setBounds(50,30,350,50);
        hello.setFont(helloFont);
        hello.setBounds(920,15,350,50);
        loguot.setBounds(1050,900,100,30);
        add(title);
        add(hello);
        add(loguot);
        setLayout(null);
        setVisible(true);
        setSize(1200,1000);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        loguot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                new Login();
            }
        });
    }


}
