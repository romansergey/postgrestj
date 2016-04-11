package org.postgrestj;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.postgrestj.config.AppConfiguration;
import org.sql2o.Sql2o;

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
                bind(new Sql2o(config.getDataSource())).to(Sql2o.class);
                bindAsContract(CRUDQueryBuilder.class);
                bindAsContract(DataAccess.class);
                bindAsContract(RecordStore.class);
            }
        });
        environment.jersey().register(CRUDResource.class);
    }
}
