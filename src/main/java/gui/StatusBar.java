package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class StatusBar extends JPanel {


    public LocalDateTime now = null;
    private JTextField tfDate;

    @Override
    public void setBackground(Color bg) {
        super.setBackground(new Color(216, 232, 255));
    }

    public StatusBar() {

        String DT = "Date and time: ";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        tfDate = new JTextField(DT+ sdf.format(new Date()));
        tfDate.setEditable(false);
        tfDate.setBackground(new Color(249, 255, 255));

        new Timer(1000, new ActionListener() // setujemo menjanje vremena
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                String formatDateTime = now.format(formatter);
                tfDate.setText("Date and time: " + formatDateTime);
            }
        }).start();


        add(tfDate);

    }
}
