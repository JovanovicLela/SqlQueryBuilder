package database;

import resource.DBNode;
import resource.data.Row;
import resource.implementation.Attribute;

import java.util.List;

public interface Repository {

    DBNode getSchema();

    List<Row> get(String from);

    List<Row> insert(String tableName, List<String> columns, List<String> values);

    List<Row> select( String tableName, String request);
    List<Row> selectExport( String tableName, String request);
    List<Row> insertImport(String tableName, String query);

    List<Row> insert(String tableName, String query);

    List<Row> delete(String tableName, String query);

    List<Row> update(String tableName, String query);

    List<Attribute> getListOfAttributes();

}
