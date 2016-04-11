package org.postgrestj.model;

import lombok.Value;

/**
 * Created by @romansergey on 4/10/16.
 */
@Value
public class TablePrimaryKeyDef {
    private String tableSchema;
    private String tableName;
    private String primaryKey;
}
