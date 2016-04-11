package org.postgrestj;

import org.junit.Test;
import org.postgrestj.CRUDQueryBuilder;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by @romansergey on 4/10/16.
 */
public class CRUDQueryBuilderTest {

    private CRUDQueryBuilder crudQueryBuilder = new CRUDQueryBuilder();

    @Test
    public void creates_byId_select_query() {
        String query = crudQueryBuilder.selectSingleById("names", "id");
        assertEquals("SELECT * FROM names WHERE id = ?", query);
    }

    @Test
    public void create_listing_select_query() {
        String query = crudQueryBuilder.selectAll("names");
        assertEquals("SELECT * FROM names", query);
    }

    @Test
    public void create_insert_query_for_a_limited_set_of_fields() {
        String query = crudQueryBuilder.insertSingle("names", Arrays.asList("name", "gender"));
        assertEquals("INSERT INTO names(name, gender) values(?, ?)", query);
    }

    @Test
    public void create_delete_query_for_a_single_Record() {
        String query = crudQueryBuilder.deleteSingle("names", "uuid");
        assertEquals("DELETE FROM names WHERE uuid = ?", query);
    }

    @Test
    public void create_update_query() {
        String query = crudQueryBuilder.update("names", "uuid", Arrays.asList("gender", "trending"));
        assertEquals("UPDATE names SET gender = ?, trending = ? WHERE uuid = ?", query);
    }


}
