package org.postgrestj;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.postgrestj.exceptions.*;
import org.postgrestj.model.TableColumnDescription;
import org.postgrestj.model.TableDescription;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by @romansergey on 4/10/16.
 */
public class RecordStore {


    private Sql2o sql2o;
    private Map<String, TableDescription> schema;

    @Inject
    public RecordStore(Sql2o sql2o) {
        this.sql2o = sql2o;
        this.schema = new DbStructureReader(sql2o).getDescription();
    }

    public Map<String, Object> getById(String tableName, Object id) {


        TableDescription tableDescription = schema.get(tableName);
        String primaryKey = tableDescription.getPrimaryKey()
                .orElseThrow(() -> new NoPrimaryKeyException());

        //String typedId = getTypedColumnValue(tableName, primaryKey, id);

        String query = String.format("SELECT * FROM %s WHERE %s = :id LIMIT 1", tableName, primaryKey);
        Map<String, Object> fields = sql2o.open()
                .createQuery(query)
                .addParameter("id", id)
                .executeAndFetchTable()
                .asList()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RecordNotFoundException(tableName, id));

        return fields.keySet().stream()
                .map(k -> Pair.of(k, fields.get(k)))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    public void update(String tableName, Object id, Map<String, Object> fields) {
        TableDescription tableDescription = schema.get(tableName);
        String primaryKey = tableDescription.getPrimaryKey()
                .orElseThrow(() -> new NoPrimaryKeyException());
        Map<String, TableColumnDescription> columns = tableDescription.getColumns().stream()
                .collect(Collectors.toMap(TableColumnDescription::getName, Function.identity()));

        String setters = fields.keySet().stream()
                .map(c -> c + " = :" + c)
                .reduce((acc, s) -> acc + ", " + s)
                .orElseThrow(() -> new IllegalArgumentException("columns should not be empty"));

        String queryString = String.format("UPDATE %s SET %s WHERE %s = :_id", tableName, setters, primaryKey);

        try(Connection connection = sql2o.open()) {
            Query query = connection
                    .createQuery(queryString)
                    .addParameter("_id", id);

            Optional<String> unknownColumn = fields.keySet().stream().filter(f -> ! columns.containsKey(f)).findFirst();

            if(unknownColumn.isPresent()) {
                throw new UnknownFieldException(unknownColumn.get());
            }



            if(addParamsFunction(fields.entrySet()).apply(query).executeUpdate().getResult() == 0) {
                throw new UpdateFailedException();
            }
        }
    }

    public Object create(String tableName, Map<String, Object> fields) {
        TableDescription tableDescription = schema.get(tableName);
        String primaryKey = tableDescription.getPrimaryKey()
                .orElseThrow(() -> new NoPrimaryKeyException());
        List<String> names = new ArrayList<>(fields.keySet());
        String namesPart = names.stream().collect(Collectors.joining(", "));
        String placeHoldersPart = names.stream().map(n -> ":" + n).collect(Collectors.joining(", "));

        String queryString = String.format("INSERT INTO %s(%s) values(%s) RETURNING %s", tableName, namesPart, placeHoldersPart, primaryKey);

        try(Connection connection = sql2o.open()) {
            Query query = connection.createQuery(queryString);
            return addParamsFunction(fields.entrySet()).apply(query).executeAndFetchTable().asList().get(0).get(primaryKey);
        }
    }




    Function<Query, Query> addParamsFunction(Set<Map.Entry<String, Object>> entrySet) {
        return entrySet
                .stream()
                .map(e ->
                                (Function<Query, Query>) (q -> q.addParameter(e.getKey(), e.getValue()))
                )
                .reduce((acc, q) -> acc.andThen(q)).orElse(Function.<Query>identity());
    }

    private String commaSep(List<String> columns) {
        return StringUtils.join(columns, ", ");
    }

    private String getPlaceHolders(int size) {
        return StringUtils.join(Collections.nCopies(size, '?'), ", ");
    }

    public TableColumnDescription getColumnDescription(TableDescription tableDescription, String columnName) {
        return tableDescription.getColumns()
                .stream()
                .filter(c -> columnName.equals(c.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException());
    }



    public void remove(String tableName, Object id) {
        String primaryKeyName = getPrimaryKeyColumnName(tableName);
        String querySting = String.format("DELETE FROM %s WHERE %s = :id", tableName, primaryKeyName);

        try(Connection connection = sql2o.open()) {
            if(connection.createQuery(querySting).addParameter("id", id).executeUpdate().getResult() == 0) {
                throw new DeleteFailedException();
            }
        }
    }

    private String getPrimaryKeyColumnName(String tableName) {
        TableDescription tableDescription = schema.get(tableName);
        return tableDescription.getPrimaryKey()
                .orElseThrow(() -> new NoPrimaryKeyException());
    }

    public Map<String, TableDescription> getSchema() {
        return schema;
    }
}
