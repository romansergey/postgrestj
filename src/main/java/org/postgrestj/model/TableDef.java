package org.postgrestj.model;

import lombok.Value;

/**
 * Created by @romansergey on 4/10/16.
 */
@Value
public class TableDef {
    private String schema;
    private String name;
    private boolean insertable;
}
