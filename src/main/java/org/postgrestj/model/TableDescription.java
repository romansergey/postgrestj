package org.postgrestj.model;

import lombok.Value;

import java.util.List;
import java.util.Optional;

/**
 * Created by @romansergey on 4/10/16.
 */
@Value
public class TableDescription {
    private String schema;
    private String name;
    private Optional<String> primaryKey;
    private List<TableColumnDescription> columns;
}
