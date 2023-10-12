package gui;

import actions.ActionManager;
import app.AppCore;
import checker.Checker;
import lombok.Data;
import observer.Notification;
import observer.Subscriber;
import tree.implementation.SelectionListener;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@Data
public class MainFrame extends JFrame implements Subscriber {

    private static MainFrame instance = null;

    private AppCore appCore;
    private ActionManager actionManager;

    private JTable jTable;
    private JScrollPane jsp;
    private JTree jTree;
    private JPanel left;

    private JScrollPane scrollPane1,scrollPane2;
    private JTextPane jtpTextArea;
    private JTextPane textPane;
    private gui.StatusBar statusBar;
    private JButton btnRun, btnImport, btnExport, btnPretty;


    private MainFrame() {

    }

    public static MainFrame getInstance(){
        if (instance==null){
            instance=new MainFrame();
            instance.initialise();
        }
        return instance;
    }


    private void initialise() {

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                Object[] opcije;
                opcije = new Object[]{"Cancel", "Exit"};
                int odgovor = JOptionPane.showOptionDialog(MainFrame.getInstance(), "Do you want to exit workspace?", "Confirm exit",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, opcije, opcije[1]);
                if (odgovor == JOptionPane.YES_OPTION || odgovor == JOptionPane.DEFAULT_OPTION) {
                    MainFrame.getInstance().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
                if (odgovor == JOptionPane.NO_OPTION) {
                    System.exit(0);
                }

            }
        });

        setTitle("SQL Query Builder");
        setResizable(false);
        getContentPane().setBackground(new Color(216, 232, 255));
        getContentPane().setForeground(UIManager.getColor("TextPane.selectionForeground"));
        setBounds(100, 100, 845, 725);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);

        actionManager = new ActionManager();

        scrollPane1 = new JScrollPane();
        scrollPane1.setBounds(30, 130, 759, 215);
        add(scrollPane1);
        jtpTextArea = new JTextPane();
        jtpTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 17));
        scrollPane1.setViewportView(jtpTextArea);

        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setForeground(new Color(5, 45, 107));
        textPane.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
        textPane.setBackground(new Color(216, 232, 255));
        textPane.setText("My Query");
        textPane.setBounds(331, 10, 168, 43);
        add(textPane);

        statusBar = new gui.StatusBar();
        statusBar.setBounds(32, 82, 222, 27);
        add(statusBar);

        btnRun = new JButton(actionManager.getRunAction());
        btnRun.setFont(new Font(Font.DIALOG, Font.BOLD, 13));
        btnRun.setBackground(new Color(0, 162, 162));
        btnRun.setForeground(Color.WHITE);
        btnRun.setBounds(687, 77, 100, 37);
        add(btnRun);

        btnImport = new JButton(actionManager.getImportAction());
        btnImport.setFont(new Font(Font.DIALOG, Font.BOLD, 13));
        btnImport.setBackground(new Color(22, 101, 220));
        btnImport.setForeground(Color.WHITE);
        btnImport.setBounds(574, 77, 100, 37);
        add(btnImport);

        btnExport = new JButton(actionManager.getExportAction());
        btnExport.setFont(new Font(Font.DIALOG, Font.BOLD, 13));
        btnExport.setBackground(new Color(22, 101, 220));
        btnExport.setForeground(Color.WHITE);
        btnExport.setBounds(461, 77, 100, 37);
        add(btnExport);

        btnPretty = new JButton(actionManager.getPrettyAction());
        btnPretty.setFont(new Font(Font.DIALOG, Font.BOLD, 13));
        btnPretty.setBackground(new Color(125, 74, 236));
        btnPretty.setForeground(Color.WHITE);
        btnPretty.setBounds(348, 77, 100, 37);
        add(btnPretty);


        jTable = new JTable();
        jTable.setPreferredScrollableViewportSize(new Dimension(500, 400));
        jTable.setFillsViewportHeight(true);
        this.add(new JScrollPane(jTable));

        scrollPane2 = new JScrollPane();
        scrollPane2.setBounds(30, 375, 762, 277);
        add(scrollPane2);

        this.setLocationRelativeTo(null);
        this.setVisible(true);


    }

    public void setAppCore(AppCore appCore) {
        this.appCore = appCore;
        this.appCore.addSubscriber(this);
        this.jTable.setModel(appCore.getTableModel());
        initialiseTree();
    }

    private void initialiseTree() {
        DefaultTreeModel defaultTreeModel = appCore.loadResource();
        jTree = new JTree(defaultTreeModel);
        jTree.addTreeSelectionListener(new SelectionListener());
        jsp = new JScrollPane(jTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        left = new JPanel(new BorderLayout());
        left.add(jsp, BorderLayout.CENTER);
        add(left, BorderLayout.WEST);
        // pack();
    }


    @Override
    public void update(Notification notification) {


    }
}
