import javax.swing.*;
import java.awt.Font;

class Admin extends JFrame{
    JLabel title;
    Admin(){
        Font f=new Font("Arial",Font.BOLD,26);
        title=new JLabel("Panel administratora");
        title.setFont(f);
        title.setBounds(50,30,350,50);
        add(title);
        setLayout(null);
        setVisible(true);
        setSize(1200,1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


}