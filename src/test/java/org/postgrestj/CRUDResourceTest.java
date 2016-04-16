package org.postgrestj;

import io.dropwizard.testing.junit.ResourceTestRule;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by @romansergey on 4/13/16.
 */
public class CRUDResourceTest {

    private static final StringKeyRecordStoreAdapter recordStore = mock(StringKeyRecordStoreAdapter.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new CRUDResource(recordStore))
            .build();

    @Test
    public void retrieves_entity_by_id() {
        when(recordStore.getById(eq("names"), eq("1"))).thenReturn(sampleRecord);

        Name nameRecord = resources.client().target("/names/1").request().get(Name.class);
        assertThat(nameRecord.name, is("Thomas"));
        assertThat(nameRecord.short_version, is("Tom"));
    }

    @Test
    public void deletes_entity_by_id() {
        Response response = resources.client().target("/names/1").request().delete();
        assertThat(response.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
        verify(recordStore, times(1)).remove(eq("names"), eq("1"));
    }

    @Test
    public void updates_entity() {
        Response response = resources.client().target("/names/1").request().post(Entity.json(sampleRecord));
        assertThat(response.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
        verify(recordStore, times(1)).update(eq("names"), eq("1"), eq(sampleRecord));
    }

    @Test
    public void creates_entity() {
        Response response = resources.client().target("/names").request().post(Entity.json(new HashMap<String, Object>() {{
            put("name", "Gregory");
            put("short_version", "Greg");
        }}));
        assertThat(response.getStatus(), is(Response.Status.CREATED.getStatusCode()));
        verify(recordStore, times(1)).update(eq("names"), eq("1"), eq(sampleRecord));
    }

    private Map<String, Object> sampleRecord = new HashMap<String, Object>() {{
        put("id", 1);
        put("name", "Thomas");
        put("short_version", "Tom");
    }};

    private static class Name {
        public String id;
        public String name;
        public String short_version;
    }

}
