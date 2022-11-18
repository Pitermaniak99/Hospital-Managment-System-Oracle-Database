import javax.swing.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Admin extends JFrame{
    JLabel title;
    JButton loguot;
    Admin(){
        Font f=new Font("Arial",Font.BOLD,26);
        title=new JLabel("Panel administratora");
        loguot=new JButton("Wyloguj");
        title.setFont(f);
        title.setBounds(50,30,350,50);
        loguot.setBounds(1050,900,100,30);
        add(title);
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