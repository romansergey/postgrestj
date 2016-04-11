package org.postgrestj;

import java.util.function.Supplier;

/**
 * Created by @romansergey on 4/10/16.
 */
public class RecordNotFoundException extends RuntimeException {
    public RecordNotFoundException(String tableName, String id) {
    }
}
