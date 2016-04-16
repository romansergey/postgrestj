package org.postgrestj;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.postgrestj.config.AppConfiguration;
import org.postgrestj.exceptions.RecordNotFoundException;
import org.postgrestj.exceptions.RecordNotFoundExceptionMapper;
import org.sql2o.QuirksMode;
import org.sql2o.Sql2o;
import org.sql2o.quirks.PostgresQuirks;

/**
 * Created by @romansergey on 4/10/16.
 */
public class ServerApplication extends Application<AppConfiguration> {

    public static void main(String[] args) throws Exception {
        new ServerApplication().run(args);
    }

    @Override
    public void run(AppConfiguration config, Environment environment) throws Exception {
        environment.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(getSql2o(config, environment)).to(Sql2o.class);
                bindAsContract(RecordStore.class);
                bindAsContract(StringKeyRecordStoreAdapter.class);
            }
        });
        environment.jersey().register(CRUDResource.class);
        environment.jersey().register(RecordNotFoundExceptionMapper.class);
    }

    protected Sql2o getSql2o(AppConfiguration config, Environment environment) {
        return new Sql2o(config.getDataSourceFactory().build(environment.metrics(), "default"), new PostgresQuirks());
    }
}
