package org.postgrestj.exceptions;

import lombok.Getter;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.function.Supplier;

/**
 * Created by @romansergey on 4/10/16.
 */
@Getter
public class RecordNotFoundException extends RuntimeException {
    private String tableName;
    private Object id;

    public RecordNotFoundException(String tableName, Object id) {
        this.tableName = tableName;
        this.id = id;
    }

    public static ExceptionMapper<RecordNotFoundException> mapper() {
        return e -> Response.status(Response.Status.NOT_FOUND).build();
    }

}
