package app;

import checker.Checker;
import com.sun.source.tree.ReturnTree;
import database.Database;
import database.DatabaseImplementation;
import database.MYSQLrepository;
import database.settings.Settings;
import database.settings.SettingsImplementation;
import gui.table.TableModel;
import lombok.Getter;
import lombok.Setter;
import observer.Notification;
import observer.enums.NotificationCode;
import observer.implementation.PublisherImplementation;
import resource.DBNodeComposite;
import resource.implementation.Attribute;
import resource.implementation.InformationResource;
import tree.Tree;
import tree.implementation.TreeImplementation;
import utils.Constants;

import javax.swing.tree.DefaultTreeModel;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@Setter
public class AppCore extends PublisherImplementation {


    private Database database;
    private Settings settings;
    private TableModel tableModel;
    private DefaultTreeModel defaultTreeModel;
    private Tree tree;
    private  InformationResource ir;
    private List<Attribute> listOfAttributes;
    Checker checker;

    public AppCore() {
        this.settings = initSettings();
        this.database = new DatabaseImplementation(new MYSQLrepository(this.settings));
        this.tableModel = new TableModel();
        this.tree = new TreeImplementation();
        this.checker = new Checker();
    }

    private Settings initSettings() {
        Settings settingsImplementation = new SettingsImplementation();
        settingsImplementation.addParameter("mysql_ip", Constants.MYSQL_IP);
        settingsImplementation.addParameter("mysql_database", Constants.MYSQL_DATABASE);
        settingsImplementation.addParameter("mysql_username", Constants.MYSQL_USERNAME);
        settingsImplementation.addParameter("mysql_password", Constants.MYSQL_PASSWORD);
        return settingsImplementation;
    }


    public DefaultTreeModel loadResource(){
        ir = (InformationResource) this.database.loadResource();
        return this.tree.generateTree(ir);
    }
    public DBNodeComposite getRoot(){
        return this.ir;
    }

    public void readDataFromTable(String fromTable){

        tableModel.setRows(this.database.readDataFromTable(fromTable));

        //Zasto ova linija moze da ostane zakomentarisana?
        this.notifySubscribers(new Notification(NotificationCode.DATA_UPDATED, this.getTableModel()));
    }
    public void  select (String nameOfTable, String request) {
        tableModel.setRowCount(0);
        tableModel.setRows(this.database.select(nameOfTable, request));
    }

    public void  selectExport (String nameOfTable, String request) {
        this.database.selectExport(nameOfTable, request);
    }

    public void insert(String tableName, String query){
        tableModel.setRowCount(0);
        tableModel.setRows(this.database.insert(tableName, query));
    }

    public void insertImport(String tableName, String query){
        //tableModel.setRowCount(0);
      this.database.insertImport(tableName, query);
    }
    public void update (String tableName, String query) {
        tableModel.setRowCount(0);
        tableModel.setRows(this.database.update(tableName, query));
    }

    public void delete (String tableName, String query) {
        tableModel.setRowCount(0);
        tableModel.setRows(this.database.delete(tableName, query));
    }
//    public void insert(String tableName, List<String> columns, List<String> values){
//        System.out.println(columns);
//        tableModel.setRowCount(0);
//        tableModel.setRows(this.database.insert(tableName, columns, values));
//    }

    public List<Attribute> getListOfAttributes(){
        return this.database.getListOfAttributes();
    }


}
