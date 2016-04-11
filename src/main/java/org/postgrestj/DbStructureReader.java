package org.postgrestj;

import org.apache.commons.io.IOUtils;
import org.postgrestj.model.*;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by @romansergey on 4/10/16.
 */
public class DbStructureReader {

    private static final String ALL_TABLES_QUERY = getResourceAsString("db/structure/allTables.sql");
    private static final String ALL_COLUMNS_QUERY = getResourceAsString("db/structure/allColumns.sql");
    private static final String ALL_PRIMARY_KEYS_QUERY = getResourceAsString("db/structure/allPrimaryKeys.sql");

    private Sql2o sql2o;

    public DbStructureReader(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    List<TableDef> listTables() {
        try(Connection connection = sql2o.open()) {
            return connection.createQuery(ALL_TABLES_QUERY).executeAndFetch(TableDef.class);
        }
    }

    List<TablePrimaryKeyDef> listPrimaryKeys() {
        try(Connection connection = sql2o.open()) {
            return connection.createQuery(ALL_PRIMARY_KEYS_QUERY).executeAndFetch(TablePrimaryKeyDef.class);
        }
    }

    List<TableColumnDef> listTableColumns() {
        try(Connection connection = sql2o.open()) {
            return connection.createQuery(ALL_COLUMNS_QUERY).executeAndFetch(TableColumnDef.class);
        }
    }


    private static String getResourceAsString(String name) {
        try {
            return IOUtils.toString(DbStructureReader.class.getClassLoader().getResourceAsStream(name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, TableDescription> getDescription() {
        List<TableDef> tableDefs = listTables();
        Map<String, List<TablePrimaryKeyDef>> primaryKeyDefs = listPrimaryKeys().stream().collect(Collectors.groupingBy(TablePrimaryKeyDef::getTableName));
        Map<String, List<TableColumnDef>> columnDefs = listTableColumns().stream().collect(Collectors.groupingBy(TableColumnDef::getTableName));

        return tableDefs.stream()
                .map(d ->
                        new TableDescription(
                                d.getSchema(),
                                d.getName(),
                                Optional.ofNullable(primaryKeyDefs.get(d.getName()))
                                        .orElse(Collections.emptyList())
                                        .stream()
                                        .findFirst()
                                        .map(TablePrimaryKeyDef::getPrimaryKey),
                                Optional.ofNullable(columnDefs.get(d.getName()))
                                        .orElse(Collections.emptyList())
                                        .stream()
                                        .map(cd ->
                                                new TableColumnDescription(
                                                        cd.getName(),
                                                        cd.isUpdatable(),
                                                        Optional.ofNullable(cd.getMaxLen()),
                                                        cd.isNullable(),
                                                        cd.getColType()
                                                )
                                        ).collect(Collectors.toList())
                        ))
                .collect(Collectors.toMap(TableDescription::getName, Function.identity()));
    }
}
