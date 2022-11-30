import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.sql.SQLException;
import java.util.ArrayList;


//OUR MAIN CLASS
public class ButtonColumn extends JFrame {

    JScrollPane pane;
    JFrame doctorFrame;
    int removedId;

    public ButtonColumn(JFrame frame, String[][] inputData, String[] inputHeaders, int tableWidth, int tableHeight){
        //FORM TITLE
        super("Button Column Example");
        doctorFrame = frame;
        //CREATE OUR TABLE AND SET HEADER
        JTable table=new JTable(inputData, inputHeaders);
        int lastColumn = inputHeaders.length - 1;

        //SET CUSTOM RENDERER TO TEAMS COLUMN
        table.getColumnModel().getColumn(lastColumn).setCellRenderer(new ButtonRenderer());;

        //SET CUSTOM EDITOR TO TEAMS COLUMN
        ButtonEditor buttonEditor = new ButtonEditor(new JTextField(), doctorFrame);
        table.getColumnModel().getColumn(lastColumn).setCellEditor(buttonEditor);

        //SCROLLPANE,SET SZE,SET CLOSE OPERATION
        table.setRowHeight(26);
        table.getTableHeader().setFont(new Font("Serif", Font.BOLD, 18));
        table.setFont(new Font("Serif", Font.PLAIN, 18));

        pane=new JScrollPane(table);
    }

    JScrollPane getPane(){
        return pane;
    }

//    int getRemovedId(){
//
//    }

}

//BUTTON RENDERER CLASS
class ButtonRenderer extends JButton implements  TableCellRenderer
{

    //CONSTRUCTOR
    public ButtonRenderer() {
        //SET BUTTON PROPERTIES
//        setOpaque(true);
    }
    @Override
    public Component getTableCellRendererComponent(JTable table, Object obj,
                                                   boolean selected, boolean focused, int row, int col) {

        //SET PASSED OBJECT AS BUTTON TEXT
        setText((obj==null) ? "":obj.toString());

        return this;
    }

}

//BUTTON EDITOR CLASS
class ButtonEditor extends DefaultCellEditor
{
    protected JButton btn;
    private String lbl;
    private Boolean clicked;


    public ButtonEditor(JTextField txt, JFrame frame) {
        super(txt);

        btn=new JButton();
        btn.setOpaque(true);

        //WHEN BUTTON IS CLICKED
        btn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String appID = lbl.toString().replace("ID wizyty: ", "");
                setUnvisibleFrame(frame);
//                System.out.println(appID);
//                JOptionPane.showMessageDialog(btn, lbl+" Clicked");
                fireEditingStopped();
                try {
                    Report report = new Report(Integer.parseInt(appID));
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    void setUnvisibleFrame(JFrame frame){
        frame.setVisible(false);
    }

    //OVERRIDE A COUPLE OF METHODS
    @Override
    public Component getTableCellEditorComponent(JTable table, Object obj,
                                                 boolean selected, int row, int col) {

        //SET TEXT TO BUTTON,SET CLICKED TO TRUE,THEN RETURN THE BTN OBJECT
        lbl=(obj==null) ? "":obj.toString();
        btn.setText(lbl);
        clicked=true;
        return btn;
    }

    //IF BUTTON CELL VALUE CHANGES,IF CLICKED THAT IS
    @Override
    public Object getCellEditorValue() {

        clicked=false;
        return new String(lbl);
    }

    @Override
    public boolean stopCellEditing() {

        //SET CLICKED TO FALSE FIRST
        clicked=false;
        return super.stopCellEditing();
    }

    @Override
    protected void fireEditingStopped() {
        // TODO Auto-generated method stub
        super.fireEditingStopped();
    }
}
