package org.postgrestj.config;

import io.dropwizard.Configuration;

import javax.sql.DataSource;

/**
 * Created by @romansergey on 4/10/16.
 */
public class AppConfiguration extends Configuration {
    public DataSource getDataSource() {
        return null;
    }
}
