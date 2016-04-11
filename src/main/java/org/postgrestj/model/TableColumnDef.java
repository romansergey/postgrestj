package org.postgrestj.model;

import lombok.Builder;
import lombok.Value;

/**
 * Created by @romansergey on 4/10/16.
 */
@Value @Builder
public class TableColumnDef {
    private String schema;
    private String tableName;
    private String name;
    private Integer position;
    private boolean nullable;
    private String colType;
    private boolean updatable;
    private Integer maxLen;
    private Integer precision;
    private String defaultValue;
    private String valueEnum;
}
