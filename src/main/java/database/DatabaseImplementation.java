package database;

import lombok.AllArgsConstructor;
import lombok.Data;
import resource.DBNode;
import resource.data.Row;
import resource.implementation.Attribute;

import java.util.List;

@Data
@AllArgsConstructor
public class DatabaseImplementation implements Database {

    private Repository repository;


    @Override
    public DBNode loadResource() {
        return repository.getSchema();
    }

    @Override
    public List<Row> insert(String tableName, List<String> columns, List<String> values) {
        return repository.insert(tableName, columns, values);
    }
    @Override
    public List<Row> insert(String tableName, String query) {
        return repository.insert(tableName, query);
    }

    @Override
    public List<Row> insertImport(String tableName, String query) {
        return repository.insertImport(tableName, query);
    }
    @Override
    public List<Row> select( String tableName, String request) {
        return repository.select(tableName, request);
    }

    @Override
    public List<Row> selectExport( String tableName, String request) {
        return repository.selectExport(tableName, request);
    }

    @Override
    public List<Row> delete(String tableName, String query) {
        return this.repository.delete(tableName, query);
    }

    @Override
    public List<Row> update(String tableName, String query) {
        return repository.update(tableName, query);
    }

    @Override
    public List<Attribute> getListOfAttributes() {
        return repository.getListOfAttributes();
    }

    @Override
    public List<Row> readDataFromTable(String tableName) {
        return repository.get(tableName);
    }
}
