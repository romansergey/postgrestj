package org.postgrestj;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Map;

/**
 * Created by @romansergey on 4/10/16.
 */
@Path("{entity}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CRUDResource {

    private RecordStore recordStore;

    @Inject
    public CRUDResource(RecordStore recordStore) {
        this.recordStore = recordStore;
    }

    @GET
    @Path("{id}")
    public Map<String, Object> read(@PathParam("id") String id) {
        return recordStore.getById("entity", id);
    }

}
