package actions;

import checker.MainWords;
import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.github.vertical_blank.sqlformatter.languages.Dialect;
import gui.MainFrame;
import gui.Query;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PrettyAction extends AbstractAction {

    HashMap<String, Color> myMap = new HashMap<String, Color>();

    public PrettyAction() {
        this("Pretty");
    }

    public PrettyAction(String name) {
        super(name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String flag = "";

        JTextPane jTextPaneQuery = MainFrame.getInstance().getJtpTextArea();
        StyledDocument doc = jTextPaneQuery.getStyledDocument();

        Style style = jTextPaneQuery.addStyle("I'm a Style", null);
        //StyleConstants.setForeground(style, Color.MAGENTA);

        Query query = new Query();
        query.setQuery(jTextPaneQuery.getText());

        String prettySql = SqlFormatter.of(Dialect.MySql).format(query.getQuery());

        for (MainWords mainWords : MainWords.values()) {
            myMap.put(mainWords.toString().toLowerCase(), Color.MAGENTA);
        }


        List<Chunk> chunks = getColorsBasedOnText(prettySql, jTextPaneQuery);
        try {
            doc.insertString(doc.getLength(), "\n\n", style);

            for (Chunk chunk : chunks) {
                doc.insertString(doc.getLength(), chunk.text + "\n", chunk.style);

            }

        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }


    }

    private ArrayList<Chunk> getColorsBasedOnText(String text, JTextPane textPane) {
        ArrayList<Chunk> chunks = new ArrayList<Chunk>();
        for (String word: text.split("\n")) {
            Chunk chunk = new Chunk();
            chunk.text = word.toUpperCase();
            Color color =  myMap.get(word);
            if (color != null) {
                chunk.style = textPane.addStyle("Style", null);
                StyleConstants.setForeground(chunk.style, color);
            }
            chunks.add(chunk);
        }
        return chunks;
    }

    private class Chunk {
        public String text;
        public Style style;
    }



}
