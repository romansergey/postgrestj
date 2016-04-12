package org.postgrestj;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.postgrestj.CRUDQueryBuilder;
import org.postgrestj.DataAccess;
import org.postgrestj.exceptions.DeleteFailedException;
import org.postgrestj.exceptions.UnknownFieldException;
import org.postgrestj.model.TableColumnDescription;
import org.postgrestj.model.TableDescription;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Created by @romansergey on 4/10/16.
 */
public class RecordStoreTest {

    @Rule
    public DbResource dbResource = new DbResource();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private RecordStore recordStore;
    private Sql2o sql2o;

    @Before
    public void setup() {
        sql2o = new Sql2o(
                String.format("jdbc:postgresql://%s:%s/%s", dbResource.config().net().host(), dbResource.config().net().port(), dbResource.config().storage().dbName()),
                "test",
                "test"
        );
        seedData(sql2o);

        recordStore = new RecordStore(sql2o, tableDescriptions());
    }

    @Test
    public void gets_record_byId_1() {
        Map<String, Object> record = recordStore.getById("names", 1);
        assertThat(record.get("name"), is("Thomas"));
    }

    @Test
    public void gets_record_byId_2() {
        Map<String, Object> record = recordStore.getById("names", 2);
        assertThat(record.get("short_version"), is("Josh"));
    }

    @Test
    public void updates_record_byId_1() {
        recordStore.update("names", 1, new HashMap<String, Object>() {{
            put("short_version", "Tommy");
        }});

        try(Connection connection = sql2o.open()) {
            String short_name = connection.createQuery("SELECT short_version FROM names where id=1").executeAndFetch(String.class).get(0);
            assertThat(short_name, is("Tommy"));
        }
    }

    @Test
    public void updates_record_byId_2() {
        recordStore.update("names", 1, new HashMap<String, Object>() {{
            put("name", "Joshuaaaaa");
            put("short_version", "Joshy");
        }});

        try(Connection connection = sql2o.open()) {
            String name = connection.createQuery("SELECT name FROM names where id=1").executeAndFetch(String.class).get(0);
            assertThat(name, is("Joshuaaaaa"));
        }
    }

    @Test
    public void updates_record_byId_fails_on_unknown_field() {
        String unknownFieldName = "unknown_field";
        expectedException.expect(UnknownFieldException.class);
        recordStore.update("names", 1, new HashMap<String, Object>() {{
            put(unknownFieldName, "abcde");
        }});
    }

    @Test
    public void inserts_record_1() {
        Object primaryKey = recordStore.create("names", new HashMap<String, Object>() {{
            put("name", "Jonathan");
            put("short_version", "Jon");
        }});

        assertThat(primaryKey, instanceOf(Integer.class));
        try(Connection connection = sql2o.open()) {
            String name = connection.createQuery("SELECT name FROM names where id=:id")
                    .addParameter("id", primaryKey)
                    .executeAndFetch(String.class).get(0);
            assertThat(name, is("Jonathan"));
        }
    }

    @Test
    public void inserts_record_2() {
        Object primaryKey = recordStore.create("names", new HashMap<String, Object>() {{
            put("name", "Craig");
        }});

        assertThat(primaryKey, instanceOf(Integer.class));
    }

    @Test
    public void deletes_record_1() {
        recordStore.remove("names", 1);

        try(Connection connection = sql2o.open()) {
            Integer recordCount = connection.createQuery("SELECT COUNT(*) FROM names where id=:id")
                    .addParameter("id", 1)
                    .executeAndFetch(Integer.class).get(0);
            assertThat(recordCount, is(0));
        }
    }

    @Test
    public void fails_on_non_existent_record() {
        expectedException.expect(DeleteFailedException.class);
        recordStore.remove("names", 123);
    }

    private Map<String, TableDescription> tableDescriptions() {
        return new HashMap<String, TableDescription>() {{
           put("names", namesTable());
        }};
    }

    private void seedData(Sql2o sql2o) {
        String[] seed = new String[] {
                "CREATE TABLE names(id serial primary key, name text, short_version text)",
                "INSERT INTO names values(nextval('names_id_seq'::regclass), 'Thomas', 'Tom')",
                "INSERT INTO names values(nextval('names_id_seq'::regclass), 'Joshua', 'Josh')"
        };
        try(Connection connection = sql2o.open()) {
            for(String query : seed) {
                connection.createQuery(query).executeUpdate();
            }
        }
    }


    private TableDescription namesTable() {
        return new TableDescription(
                "public",
                "names",
                Optional.of("id"),
                Arrays.asList(
                        new TableColumnDescription(
                                "id",
                                false,
                                Optional.empty(),
                                true,
                                "integer"
                        ),
                        new TableColumnDescription(
                                "name",
                                false,
                                Optional.empty(),
                                true,
                                "text"
                        ),
                        new TableColumnDescription(
                                "short_version",
                                true,
                                Optional.empty(),
                                true,
                                "text"
                        )
                )
        );
    }

}
