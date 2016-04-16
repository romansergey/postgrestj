package org.postgrestj;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

/**
 * Created by @romansergey on 4/13/16.
 */
public class DataSeed {
    public static void seedData(Sql2o sql2o) {
        String[] seed = new String[] {
                "DROP TABLE IF EXISTS names",
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
}
