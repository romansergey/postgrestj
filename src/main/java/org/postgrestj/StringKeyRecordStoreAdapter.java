package org.postgrestj;

import org.postgrestj.exceptions.NoPrimaryKeyException;
import org.postgrestj.exceptions.UnknownFieldException;
import org.postgrestj.model.TableColumnDescription;
import org.postgrestj.model.TableDescription;

import javax.inject.Inject;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by @romansergey on 4/14/16.
 */
public class StringKeyRecordStoreAdapter {

    private RecordStore recordStore;

    @Inject
    public StringKeyRecordStoreAdapter(RecordStore recordStore) {
        this.recordStore = recordStore;
    }

    public Map<String, Object> getById(String tableName, String id) {
        Object typedId = getTypedKey(tableName, id);
        return recordStore.getById(tableName, typedId);
    }

    public void update(String tableName, String id, Map<String, Object> fields) {
        Object typedId = getTypedKey(tableName, id);
        recordStore.update(tableName, typedId, fields);
    }

    public String create(String tableName, Map<String, Object> fields) {
        return stringifyKey(tableName, recordStore.create(tableName, fields));
    }


    public void remove(String tableName, String id) {
        Object typedId = getTypedKey(tableName, id);
        recordStore.remove(tableName, typedId);
    }

    private Object getTypedKey(String tableName, String id) {
        TableDescription tableDescription = recordStore.getSchema().get(tableName);
        String primaryKeyFieldName = tableDescription.getPrimaryKey()
                .orElseThrow(() -> new NoPrimaryKeyException());
        return getTypedColumnValue(tableName, primaryKeyFieldName, id);
    }

    public String stringifyKey(String tableName, Object id) {
        TableDescription tableDescription = recordStore.getSchema().get(tableName);
        String primaryKeyFieldName = tableDescription.getPrimaryKey()
                .orElseThrow(() -> new NoPrimaryKeyException());
        return getTableColumnDescription(tableName, primaryKeyFieldName)
                .map(c -> stringifyKey(c, id))
                .orElseThrow(() -> new UnknownFieldException(primaryKeyFieldName));
    }

    public Object getTypedColumnValue(String tableName, String columnName, String value) {
        return getTableColumnDescription(tableName, columnName)
                .map(c -> getTypedColumnValue(value, c))
                .orElseThrow(() -> new UnknownFieldException(columnName));
    }

    public Optional<TableColumnDescription> getTableColumnDescription(String tableName, String columnName) {
        return recordStore.getSchema().get(tableName)
                .getColumns()
                .stream()
                .filter(c -> columnName.equals(c.getName()))
                .findFirst();
    }

    public Object getTypedColumnValue(String value, TableColumnDescription columnDescription) {
        if("integer".equals(columnDescription.getColumnType())) {
            return Integer.parseInt(value);
        } else {
            return value;
        }
    }

    public String stringifyKey(TableColumnDescription tableColumnDescription, Object id) {
        if("integer".equals(tableColumnDescription.getColumnType())) {
            return Integer.toString((Integer) id);
        } else {
            return id.toString();
        }
    }
}
