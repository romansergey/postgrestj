package org.postgrestj;

import org.postgrestj.CRUDQueryBuilder;
import org.postgrestj.model.TableColumnDescription;
import org.postgrestj.model.TableDescription;
import org.sql2o.Sql2o;

import java.util.Map;

/**
 * Created by @romansergey on 4/10/16.
 */
public class RecordStore {


    private DataAccess dataAccess;
    private CRUDQueryBuilder crudQueryBuilder;
    private Map<String, TableDescription> schema;

    public RecordStore(DataAccess dataAccess, CRUDQueryBuilder crudQueryBuilder, Map<String, TableDescription> schema) {
        this.dataAccess = dataAccess;
        this.crudQueryBuilder = crudQueryBuilder;
        this.schema = schema;
    }

    public Map<String, Object> getById(String tableName, String id) {
        TableDescription tableDescription = schema.get(tableName);
        String primaryKey = tableDescription.getPrimaryKey()
                .orElseThrow(() -> new NoPrimaryKeyException());

        String sql = crudQueryBuilder.selectSingleById(tableName, primaryKey);

        TableColumnDescription primaryKeyColumnDescription = getColumnDescription(tableDescription, primaryKey);

        Object idValue = getTypedColumnValue(id, primaryKeyColumnDescription);

        return dataAccess.query(sql, idValue)
                .stream().findFirst()
                .orElseThrow(() -> new RecordNotFoundException(tableName, id));
    }

    public Object getTypedColumnValue(String value, TableColumnDescription columnDescription) {
        if("integer".equals(columnDescription.getColumnType())) {
            return Integer.parseInt(value);
        } else {
            return value;
        }
    }

    public TableColumnDescription getColumnDescription(TableDescription tableDescription, String columnName) {
        return tableDescription.getColumns()
                .stream()
                .filter(c -> columnName.equals(c.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException());
    }

}
