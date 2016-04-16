package org.postgrestj;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Map;

/**
 * Created by @romansergey on 4/10/16.
 */
@Path("{entity}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CRUDResource {

    private StringKeyRecordStoreAdapter recordStore;

    @Inject
    public CRUDResource(StringKeyRecordStoreAdapter recordStore) {
        this.recordStore = recordStore;
    }

    @GET
    @Path("{id}")
    public Map<String, Object> read(@PathParam("entity") String entity, @PathParam("id") String id) {
        return recordStore.getById(entity, id);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("entity") String entity, @PathParam("id") String id) {
        recordStore.remove(entity, id);
    }

    @POST
    @Path("{id}")
    public void update(@PathParam("entity") String entity, @PathParam("id") String id, Map<String, Object> updates) {
        recordStore.update(entity, id, updates);
    }

    @POST
    public Response create(@PathParam("entity") String entity, Map<String, Object> updates) {
        String id = recordStore.create(entity, updates);
        return Response.status(Response.Status.CREATED).header("X-Created-Id", id).build();
    }

}
