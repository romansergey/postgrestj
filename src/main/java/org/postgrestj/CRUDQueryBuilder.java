package org.postgrestj;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.util.SelectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by @romansergey on 4/10/16.
 */
public class CRUDQueryBuilder {
    public String selectSingleById(String tableName, String fieldName) {
        return String.format("SELECT * FROM %s WHERE %s = ?", tableName, fieldName);
    }

    public String selectAll(String tableName) {
        return String.format("SELECT * FROM %s", tableName);
    }

    public String insertSingle(String tableName, List<String> columns) {
        String columnsCommaSep = commaSep(columns);
        String placeHolders = getPlaceHolders(columns.size());
        return String.format("INSERT INTO %s(%s) values(%s)", tableName, columnsCommaSep, placeHolders);
    }

    public String deleteSingle(String tableName, String fieldName) {
        return String.format("DELETE FROM %s WHERE %s = ?", tableName, fieldName);
    }


    public String update(String tableName, String idField, List<String> columns) {
        String setters = columns.stream()
                .map(c -> c + " = ?")
                .reduce((acc, s) -> acc + ", " + s)
                .orElseThrow(() -> new IllegalArgumentException("columns should not be empty"));

        return String.format("UPDATE %s SET %s WHERE %s = ?", tableName, setters, idField);
    }

    private String commaSep(List<String> columns) {
        return StringUtils.join(columns, ", ");
    }

    private String getPlaceHolders(int size) {
        return StringUtils.join(Collections.nCopies(size, '?'), ", ");
    }

}
