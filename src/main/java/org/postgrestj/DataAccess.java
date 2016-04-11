package org.postgrestj;

import org.sql2o.Sql2o;

import java.util.List;
import java.util.Map;

/**
 * Created by @romansergey on 4/11/16.
 */
public class DataAccess {

    private Sql2o sql2o;

    public DataAccess(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    public List<Map<String, Object>> query(String sql, Object... params) {
        return sql2o.open().createQuery(sql).withParams(params)
                .executeAndFetchTable().asList();
    }

    public int execute(String sql, Object... params) {
        return sql2o.open().createQuery(sql).withParams(params)
                .executeUpdate().getResult();
    }

}
