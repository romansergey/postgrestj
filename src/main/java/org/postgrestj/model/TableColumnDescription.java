package org.postgrestj.model;

import lombok.Value;

import java.util.Optional;

/**
 * Created by @romansergey on 4/10/16.
 */
@Value
public class TableColumnDescription {
    private String name;
    private boolean updatable;
    private Optional<Integer> maxLength;
    private boolean nullable;
    private String columnType;
}
