package org.postgrestj.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Created by @romansergey on 4/16/16.
 */
public class RecordNotFoundExceptionMapper implements ExceptionMapper<RecordNotFoundException> {
    @Override
    public Response toResponse(RecordNotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
