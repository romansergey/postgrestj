package org.postgrestj.model;

import lombok.Value;

/**
 * Created by @romansergey on 4/10/16.
 */
@Value
public class TableColumnDef {
    private String schema;
    private String tableName;
    private String name;
    private int position;
    private boolean nullable;
    private String colType;
    private boolean updatable;
    private int maxLen;
    private int precision;
    private String defaultValue;
    private String valueEnum;
}
