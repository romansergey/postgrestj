package org.postgrestj;

import org.junit.rules.ExternalResource;
import org.postgrestj.model.TableDescription;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by @romansergey on 4/13/16.
 */
public class RecordStoreRule extends ExternalResource {

    private DbResource dbResource;
    private RecordStore recordStore;
    private Sql2o sql2o;
    private Consumer<Sql2o> onDbInit;

    public RecordStoreRule(DbResource dbResource, Consumer<Sql2o> onDbInit) {
        this.onDbInit = onDbInit;
        this.dbResource = dbResource;
    }

    public RecordStore get() {
        return recordStore;
    }

    public void withConnection(Consumer<Connection> runme) {
        try(Connection connection = sql2o.open()) {
            runme.accept(connection);
        }
    }

    @Override
    protected void before() throws Throwable {
        sql2o = new Sql2o(
                getDbUrl(),
                "test",
                "test"
        );
        onDbInit.accept(sql2o);
        recordStore = new RecordStore(sql2o);
    }

    public String getDbUrl() {
        return String.format("jdbc:postgresql://%s:%s/%s", dbResource.config().net().host(), dbResource.config().net().port(), dbResource.config().storage().dbName());
    }

    @Override
    protected void after() {
        reset();
    }

    private void reset() {
        withConnection(c ->
                        c.createQuery("drop schema public cascade; create schema public").executeUpdate()
        );
    }

}
