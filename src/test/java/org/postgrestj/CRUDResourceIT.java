package org.postgrestj;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.postgrestj.exceptions.RecordNotFoundException;
import org.sql2o.Sql2o;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Created by @romansergey on 4/13/16.
 */
public class CRUDResourceIT {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Inject
    private RecordStore recordStoreInstance;

    @Inject
    private Sql2o sql2o;

    @ClassRule
    public static final AppRule appRule = new AppRule();

    private static WebTarget target;

    @BeforeClass
    public static void beforeClass() {
        target = appRule.buildClientTarget();
    }

    @Before
    public void before() {
        ServiceLocator serviceLocator = ((ServletContainer) appRule.getApp().getEnvironment().getJerseyServletContainer()).getApplicationHandler().getServiceLocator();
        serviceLocator.inject(this);
        DataSeed.seedData(sql2o);
    }

    @Test
    public void retrieves_entity_by_id() {
        Name nameRecord = target.path("/names/1").request().get(Name.class);
        assertThat(nameRecord.name, is("Thomas"));
        assertThat(nameRecord.short_version, is("Tom"));
    }

    @Test
    public void updates_entity() {
        target.path("/names/1").request().post(Entity.json(new HashMap<String, Object>() {{
            put("name", "Thomasz");
        }}));
        Map<String, Object> nameRecord = recordStoreInstance.getById("names", 1);
        assertThat(nameRecord.get("name"), is("Thomasz"));
    }

    @Test
    public void deletes_entity_by_id() {
        expectedException.expect(RecordNotFoundException.class);
        target.path("/names/1").request().delete();
        recordStoreInstance.getById("names", 1);
    }

    @Test
    public void creates_entity() {
        Response response = target.path("/names").request().post(Entity.json(
                new HashMap<String, Object>() {{
                    put("name", "Timothy");
                    put("short_version", "Tim");
                }}
        ));
        List<Object> createdIdHeaderList = response.getHeaders().get("X-Created-Id");
        assertNotNull(createdIdHeaderList);
        Optional<String> createdId = createdIdHeaderList.stream().findFirst().map(String.class::cast);
        assertTrue(createdId.isPresent());

        Map<String, Object> nameRecord = recordStoreInstance.getById("names", createdId.map(Integer::parseInt).get());
        assertThat(nameRecord.get("short_version"), is("Tim"));
    }

    @Test
    public void returns_404_when_non_existing_record_is_requested() {
        Response response = target.path("/names/123").request().get();
        assertThat(response.getStatus(), is(Response.Status.NOT_FOUND.getStatusCode()));
    }

    private static class Name {
        public String id;
        public String name;
        public String short_version;
    }

}
