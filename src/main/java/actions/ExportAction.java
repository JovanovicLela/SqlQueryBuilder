package actions;
import app.AppCore;
import gui.MainFrame;
import gui.Query;
import javax.swing.*;
import java.awt.event.ActionEvent;


public class ExportAction extends AbstractAction {

    public ExportAction() {
        this("Export");
    }

    public ExportAction(String name) {
        super(name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        AppCore appCore = MainFrame.getInstance().getAppCore();

        Query query = new Query();
        query.parse(MainFrame.getInstance().getJtpTextArea().getSelectedText());

        appCore.selectExport(query.getTableName(), query.toString());

    }


}
