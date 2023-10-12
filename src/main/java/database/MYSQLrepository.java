package database;

import com.opencsv.CSVWriter;
import database.settings.Settings;
import lombok.Data;
import resource.DBNode;
import resource.data.Row;
import resource.enums.AttributeType;
import resource.enums.ConstraintType;
import resource.implementation.Attribute;
import resource.implementation.AttributeConstraint;
import resource.implementation.Entity;
import resource.implementation.InformationResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class MYSQLrepository implements Repository{

    private static final String FILE_NAME_EXPORT = "src/main/java/res/ExportedData.csv";
    private static final String FILE_NAME_IMPORT = "src/main/java/res/NewData.csv";
    Path pathExport = Paths.get(FILE_NAME_EXPORT);
    Path pathImport = Paths.get(FILE_NAME_IMPORT);

    private Settings settings;
    private Connection connection;
    private List<Attribute> listOfAttributes = new ArrayList<>();

    public MYSQLrepository(Settings settings) {
        this.settings = settings;
    }

    private void initConnection() throws SQLException, ClassNotFoundException{
        String ip = (String) settings.getParameter("mysql_ip");
        String database = (String) settings.getParameter("mysql_database");
        String username = (String) settings.getParameter("mysql_username");
        String password = (String) settings.getParameter("mysql_password");
        //Class.forName("net.sourceforge.jtds.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://"+ip+"/"+database,username,password);


    }

    private void closeConnection(){
        try{
            connection.close();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            connection = null;
        }
    }


    @Override
    public DBNode getSchema() {
        try {
            this.initConnection();

            DatabaseMetaData metaData = connection.getMetaData();
            InformationResource ir = new InformationResource("RAF_BP_Primer");

            String tableType[] = {"TABLE"};
            ResultSet tables = metaData.getTables(connection.getCatalog(), null, null, tableType);

            while (tables.next()) {

                String tableName = tables.getString("TABLE_NAME");
                if (tableName.contains("trace")) continue;
                Entity newTable = new Entity(tableName, ir);
                ir.addChild(newTable);

                //Koje atribute imaja ova tabela?

                ResultSet columns = metaData.getColumns(connection.getCatalog(), null, tableName, null);

                while (columns.next()) {

                    String columnName = columns.getString("COLUMN_NAME");
                    String columnType = columns.getString("TYPE_NAME");
                    int columnSize = Integer.parseInt(columns.getString("COLUMN_SIZE"));
                    Attribute attribute = new Attribute(columnName, newTable, AttributeType.valueOf(Arrays.stream(columnType.toUpperCase().split(" "))
                            .collect(Collectors.joining("_"))), columnSize);
                    newTable.addChild(attribute);
                }

                ResultSet primaryKeys = metaData.getPrimaryKeys(connection.getCatalog(), null, tableName);

                while (primaryKeys.next()) {
                    String columnName = primaryKeys.getString("COLUMN_NAME");
                    String primaryKeyName = primaryKeys.getString("PK_NAME");
                    AttributeConstraint attributeConstraint = new AttributeConstraint(
                            primaryKeyName, ((Attribute) newTable.getChildByName(columnName)), ConstraintType.PRIMARY_KEY);
                    ((Attribute) newTable.getChildByName(columnName)).addChild(attributeConstraint);
                }

                ResultSet foreignKeys = metaData.getImportedKeys(connection.getCatalog(), null, tableName);

                while (foreignKeys.next()) {
                    String foreignKeyColumnName = foreignKeys.getString("FKCOLUMN_NAME");
                    String foreignKeyName = foreignKeys.getString("FK_NAME");
                    AttributeConstraint attributeConstraint = new AttributeConstraint(
                            foreignKeyName, ((Attribute) newTable.getChildByName(foreignKeyColumnName)), ConstraintType.FOREIGN_KEY);
                    ((Attribute) newTable.getChildByName(foreignKeyColumnName)).addChild(attributeConstraint);
                }
            }


            tables = metaData.getTables(connection.getCatalog(), null, null, tableType);

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                ResultSet foreignKeys = metaData.getImportedKeys(connection.getCatalog(), null, tableName);

                while (foreignKeys.next()) {
                    String foreignKeyColumnName = foreignKeys.getString("FKCOLUMN_NAME");
                    String primaryKeyTableName = foreignKeys.getString("PKTABLE_NAME");
                    String foreignKeyRefName = foreignKeys.getString("PKCOLUMN_NAME");

                    Attribute fk1 = (Attribute)((Entity)ir.getChildByName(tableName)).getChildByName(foreignKeyColumnName);
                    Attribute fk2 = (Attribute)((Entity)ir.getChildByName(primaryKeyTableName)).getChildByName(foreignKeyRefName);

                    fk1.setInRelationWith(fk2);

                    ((Entity) ir.getChildByName(tableName)).addRelation((Entity) ir.getChildByName(primaryKeyTableName));
                    ((Entity) ir.getChildByName(primaryKeyTableName)).addRelation((Entity) ir.getChildByName(tableName));
                }
            }

            return ir;
            //String isNullable = columns.getString("IS_NULLABLE");
            // ResultSet foreignKeys = metaData.getImportedKeys(connection.getCatalog(), null, table.getName());
            // ResultSet primaryKeys = metaData.getPrimaryKeys(connection.getCatalog(), null, table.getName());

        }
        catch (SQLException e1) {
            e1.printStackTrace();
        }
        catch (ClassNotFoundException e2){ e2.printStackTrace();}
        finally {
            this.closeConnection();
        }

        return null;
    }

    @Override
    public List<Row> get(String from) {

        List<Row> rows = new ArrayList<>();


        try{
            this.initConnection();

            String query = "SELECT * FROM " + from;
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            ResultSetMetaData resultSetMetaData = rs.getMetaData();

            while (rs.next()){

                Row row = new Row();
                row.setName(from);

                for (int i = 1; i<=resultSetMetaData.getColumnCount(); i++){
                    row.addField(resultSetMetaData.getColumnName(i), rs.getString(i));
                }
                rows.add(row);


            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            this.closeConnection();
        }

        return rows;
    }
    @Override
    public List<Row> insert(String tableName, List<String> columns, List<String> values) {

        List<Row> rows = new ArrayList<>();
        ResultSet resultSet = null;

        try {
            this.initConnection();
            String query = "INSERT INTO " + tableName + " (" + columns + ")" + returnValues(columns.size());
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            int columnNum = 1;
            // preparedStatement.setString(1,"Staaaa");
            for (Attribute attribute : listOfAttributes) {
                if (attribute.getParent().getName().equals(tableName)) {
                    parseColumnType(attribute.getAttributeType(),values.get(columnNum - 1), preparedStatement, columnNum);
                    columnNum++;
                }

            }

            preparedStatement.executeUpdate();


        } catch (Exception e) {
            System.out.println(columns.size());
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }


        return get(tableName);


    }

    @Override
    public List<Row> select(String tableName, String request) {
        List<Row> rows = new ArrayList<>();

        try {
            this.initConnection();

            String query = request;
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            ResultSetMetaData resultSetMetaData = rs.getMetaData();

            while (rs.next()) {

                Row row = new Row();
                row.setName(tableName);

                for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                    row.addField(resultSetMetaData.getColumnName(i), rs.getString(i));
                }
                rows.add(row);


            }
        } catch (Exception e) {
            System.out.println(request);
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }

        return rows;
    }

    @Override
    public List<Row> selectExport(String tableName, String request) {
        List<Row> rows = new ArrayList<>();

        try {
            this.initConnection();

            String query = request;
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();

            try (var writer = new com.opencsv.CSVWriter(Files.newBufferedWriter(pathExport, StandardCharsets.UTF_8),
                    com.opencsv.CSVWriter.DEFAULT_SEPARATOR, com.opencsv.CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.NO_ESCAPE_CHARACTER,
                    com.opencsv.CSVWriter.DEFAULT_LINE_END)) {

                writer.writeAll(rs, true);

            } catch (IOException | SQLException ex) {
                ex.printStackTrace();
            }

        } catch (Exception e) {
            System.out.println(request);
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }

        return rows;
    }

    @Override
    public List<Row> insertImport(String tableName, String request) {
        List<Row> rows = new ArrayList<>();


        try {
            this.initConnection();
            String query = request;
            PreparedStatement preparedStatement = connection.prepareStatement(query);


          /*  DataReader reader = new CSVReader(new File(FILE_NAME_IMPORT))
                    .setFieldNamesInFirstRow(true);

            DataWriter writer = new JdbcWriter(connection, tableName)
                    .setAutoCloseConnection(true)
                    .setBatchSize(100);

            Job.run(reader, writer);*/

        } catch (Exception e) {
            System.out.println(tableName);
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }

        return rows;
    }

    @Override
    public List<Row> insert(String tableName, String query) {
        List<Row> rows = new ArrayList<>();
        ResultSet resultSet = null;

        try {
            this.initConnection();
            String insert = query;
            PreparedStatement preparedStatement = connection.prepareStatement(insert);
            preparedStatement.executeUpdate();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }
        System.out.println(tableName);
        return get(tableName);

    }

    @Override
    public List<Row> delete(String tableName, String query) {
        try {
            this.initConnection();
            System.out.println(query);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }

        return get(tableName);

    }

    @Override
    public List<Row> update(String tableName, String query) {
        ResultSet resultSet = null;

        System.out.println(query);
        query = query.replace('\n', ' ').replace('\r', ' ');
        System.out.println(query);
        try {
            this.initConnection();
            String update = query;
            PreparedStatement preparedStatement = connection.prepareStatement(update);
            preparedStatement.executeUpdate();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }

        return get(tableName);
    }

    public String returnValues(int size){

        //"VALUES(?,?)"

        String begin = "VALUES(";
        String ends = ")";

        for(int i = 0 ; i < size - 1; i++){
            begin += "?,";
        }
        begin += "?";
        begin += ends;

        return begin;
    }

    public void parseColumnType(AttributeType attributeType, String value, PreparedStatement preparedStatement, int num) {

        System.out.println(attributeType + value
        );
        try {
            switch (attributeType) {

                case VARCHAR: {
                    preparedStatement.setString(num,value);
                    break;
                }
                case INT_UNSIGNED: {
                    System.out.println(value);
                    preparedStatement.setInt(num, Integer.parseInt(value));
                    break;
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public String parseColumnNames (List<String> columnNames) {
        String all = "";
        for(String columnName : columnNames){
            all+= columnName + ",";
        }

        all =  all.substring(0, all.length() - 1);
        return all;
    }

    public List<Attribute> getListOfAttributes() {
        return listOfAttributes;
    }
}
