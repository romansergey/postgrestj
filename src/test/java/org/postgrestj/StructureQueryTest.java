package org.postgrestj;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.postgrestj.model.TableColumnDef;
import org.postgrestj.model.TableDef;
import org.postgrestj.model.TableDescription;
import org.postgrestj.model.TablePrimaryKeyDef;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by @romansergey on 4/10/16.
 */
public class StructureQueryTest {

    @Rule
    public DbResource dbResource = new DbResource();

    private Sql2o sql2o;

    private DbStructureReader dbStructureReader;

    @Before
    public void setup() {
        sql2o = new Sql2o(
                String.format("jdbc:postgresql://%s:%s/%s", dbResource.config().net().host(), dbResource.config().net().port(), dbResource.config().storage().dbName()),
                "test",
                "test"
        );
        dbStructureReader = new DbStructureReader(sql2o);
    }

    @Test
    public void allTables_returns_table_definitions() {
        runQuery(CREATE_EMPTY_TABLE);

        List<TableDef> tableDefs = dbStructureReader.listTables();

        assertThat(tableDefs.size(), is(1));
        assertThat(tableDefs.get(0).getName(), is("empty"));
    }

    @Test
    public void allPrimaryKeys_returns_table_definitions() {
        runQuery(CREATE_WITH_PK_TABLE);

        List<TablePrimaryKeyDef> tablePrimaryKeyDefs = dbStructureReader.listPrimaryKeys();

        assertThat(tablePrimaryKeyDefs.size(), is(1));
        assertThat(tablePrimaryKeyDefs.get(0).getTableName(), is("with_pk"));
        assertThat(tablePrimaryKeyDefs.get(0).getPrimaryKey(), is("id"));
    }



    @Test
    public void allColumns_returns_column_definitions() {
        runQuery(CREATE_NAMES_TABLE);

        List<TableColumnDef> tableColumnDefs = dbStructureReader.listTableColumns();

        assertThat(tableColumnDefs.size(), is(1));

        TableColumnDef columnDef = tableColumnDefs.get(0);
        assertThat(columnDef.getTableName(), is("names"));
        assertThat(columnDef.getName(), is("name"));
        assertThat(columnDef.getColType(), is("text"));
        assertThat(columnDef.isNullable(), is(true));
        assertThat(columnDef.isUpdatable(), is(true));
        assertThat(columnDef.getDefaultValue(), is(nullValue()));
    }

    @Test
    public void getSchemaDescription_returns_list_of_table_descriptions() {
        runQuery(CREATE_EMPTY_TABLE);
        runQuery(CREATE_WITH_PK_TABLE);
        runQuery(CREATE_NAMES_TABLE);

        Map<String, TableDescription> desc = dbStructureReader.getDescription();
        assertThat(desc.get("with_pk").getPrimaryKey().get(), is("id"));
    }

    private static final String CREATE_EMPTY_TABLE = "CREATE TABLE empty()";
    private static final String CREATE_NAMES_TABLE = "CREATE TABLE names(name text)";
    private static final String CREATE_WITH_PK_TABLE = "CREATE TABLE with_pk(id serial primary key)";

    private void runQuery(String sql) {
        try(Connection connection = sql2o.open()) {
            connection.createQuery(sql).executeUpdate();
        }
    }
}
