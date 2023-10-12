package actions;

import app.AppCore;
import checker.Checker;
import gui.MainFrame;
import gui.Query;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class RunAction extends AbstractAction {

    public RunAction() {
        this("Run");
    }

    public RunAction(String name) {
        super(name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        AppCore appCore = MainFrame.getInstance().getAppCore();

        Query query = new Query();
        query.parse(MainFrame.getInstance().getJtpTextArea().getSelectedText());

        System.out.println(query.getTableName());
        Checker checker = new Checker();

        if(checker.check(query)){
            switch (query.calling){
                case "select": appCore.select(query.getTableName(), query.toString()); break;
                case "delete": appCore.delete(query.getTableName(), query.toString()); break;
                case "insert": appCore.insert(query.getTableName(), query.toString());
                case "update": appCore.update(query.getTableName(), query.toString());
            }

        }

        MainFrame.getInstance().getScrollPane2().setViewportView(MainFrame.getInstance().getJTable());
    }

}
