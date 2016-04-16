package org.postgrestj;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.postgrestj.exceptions.DeleteFailedException;
import org.postgrestj.exceptions.UnknownFieldException;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

/**
 * Created by @romansergey on 4/10/16.
 */
public class RecordStoreTest {

    @ClassRule
    public static DbResource dbResource = new DbResource();

    @Rule
    public RecordStoreRule recordStore = new RecordStoreRule(dbResource, DataSeed::seedData);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void gets_record_byId_1() {
        Map<String, Object> record = recordStore.get().getById("names", 1);
        assertThat(record.get("name"), is("Thomas"));
    }

    @Test
    public void gets_record_byId_2() {
        Map<String, Object> record = recordStore.get().getById("names", 2);
        assertThat(record.get("short_version"), is("Josh"));
    }

    @Test
    public void updates_record_byId_1() {
        recordStore.get().update("names", 1, new HashMap<String, Object>() {{
            put("short_version", "Tommy");
        }});

        recordStore.withConnection((connection) -> {
            String short_name = connection.createQuery("SELECT short_version FROM names where id=1").executeAndFetch(String.class).get(0);
            assertThat(short_name, is("Tommy"));
        });
    }

    @Test
    public void updates_record_byId_2() {
        recordStore.get().update("names", 1, new HashMap<String, Object>() {{
            put("name", "Joshuaaaaa");
            put("short_version", "Joshy");
        }});

        recordStore.withConnection((connection) -> {
            String name = connection.createQuery("SELECT name FROM names where id=1").executeAndFetch(String.class).get(0);
            assertThat(name, is("Joshuaaaaa"));
        });
    }

    @Test
    public void updates_record_byId_fails_on_unknown_field() {
        String unknownFieldName = "unknown_field";
        expectedException.expect(UnknownFieldException.class);
        recordStore.get().update("names", 1, new HashMap<String, Object>() {{
            put(unknownFieldName, "abcde");
        }});
    }

    @Test
    public void inserts_record_1() {
        Object primaryKey = recordStore.get().create("names", new HashMap<String, Object>() {{
            put("name", "Jonathan");
            put("short_version", "Jon");
        }});

        assertThat(primaryKey, instanceOf(Integer.class));
        recordStore.withConnection((connection) -> {
            String name = connection.createQuery("SELECT name FROM names where id=:id")
                    .addParameter("id", primaryKey)
                    .executeAndFetch(String.class).get(0);
            assertThat(name, is("Jonathan"));
        });
    }

    @Test
    public void inserts_record_2() {
        Object primaryKey = recordStore.get().create("names", new HashMap<String, Object>() {{
            put("name", "Craig");
        }});

        assertThat(primaryKey, instanceOf(Integer.class));
    }

    @Test
    public void deletes_record_1() {
        recordStore.get().remove("names", 1);

        recordStore.withConnection((connection) -> {
            Integer recordCount = connection.createQuery("SELECT COUNT(*) FROM names where id=:id")
                    .addParameter("id", 1)
                    .executeAndFetch(Integer.class).get(0);
            assertThat(recordCount, is(0));
        });
    }

    @Test
    public void fails_on_non_existent_record() {
        expectedException.expect(DeleteFailedException.class);
        recordStore.get().remove("names", 123);
    }



}
