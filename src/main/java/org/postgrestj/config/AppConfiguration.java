package org.postgrestj.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.sql.DataSource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created by @romansergey on 4/10/16.
 */
public class AppConfiguration extends Configuration {
    @Valid
    @NotNull
    @JsonProperty
    private DataSourceFactory database = new DataSourceFactory();

    public DataSourceFactory getDataSourceFactory() {
        return database;
    }
}
