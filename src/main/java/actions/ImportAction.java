package actions;

import app.AppCore;
import gui.MainFrame;
import gui.Query;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ImportAction extends AbstractAction {



    public ImportAction() {
        this("Import");
    }

    public ImportAction(String name) {
        super(name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        AppCore appCore = MainFrame.getInstance().getAppCore();

        Query query = new Query();
        query.parse(MainFrame.getInstance().getJtpTextArea().getSelectedText());

        // RADI
       // appCore.insertImport(query.getTableName(), query.toString());
        appCore.select(query.getTableName(), query.toString());

        MainFrame.getInstance().getScrollPane2().setViewportView(MainFrame.getInstance().getJTable());

    }
}
