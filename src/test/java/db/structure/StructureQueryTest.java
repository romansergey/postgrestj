package db.structure;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.postgrestj.model.TableColumnDef;
import org.postgrestj.model.TableDef;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by @romansergey on 4/10/16.
 */
public class StructureQueryTest {

    @ClassRule
    public static DbResource dbResource = new DbResource();

    private Sql2o sql2o;

    @Before
    public void setup() {
        sql2o = new Sql2o(
                String.format("jdbc:postgresql://%s:%s/%s", dbResource.config().net().host(), dbResource.config().net().port(), dbResource.config().storage().dbName()),
                "test",
                "test"
        );
    }

    @Test
    public void allTables_returns_table_definitions() {
        List<TableDef> tableDefs;
        try(Connection connection = sql2o.open()) {
            connection.createQuery("CREATE TABLE empty()").executeUpdate();
            tableDefs = connection.createQuery(getResourceAsString("db/structure/allTables.sql")).executeAndFetch(TableDef.class);
        }

        assertThat(tableDefs.size(), is(1));
        assertThat(tableDefs.get(0).getName(), is("empty"));
    }


    @Test
    public void allColumns_returns_column_definitions() {
        List<TableColumnDef> tableColumnDefs;
        try(Connection connection = sql2o.open()) {
            connection.createQuery("CREATE TABLE names(name text)").executeUpdate();
            tableColumnDefs = connection.createQuery(getResourceAsString("db/structure/allColumns.sql")).executeAndFetch(TableColumnDef.class);
        }

        assertThat(tableColumnDefs.size(), is(1));

        TableColumnDef columnDef = tableColumnDefs.get(0);
        assertThat(columnDef.getTableName(), is("names"));
        assertThat(columnDef.getName(), is("name"));
        assertThat(columnDef.getColType(), is("text"));
    }

    private String getResourceAsString(String name) {
        try {
            return IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
