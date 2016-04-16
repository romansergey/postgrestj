package org.postgrestj;

import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import junit.framework.Test;
import org.glassfish.jersey.client.ClientProperties;
import org.junit.rules.ExternalResource;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.postgrestj.config.AppConfiguration;
import ru.yandex.qatools.embed.postgresql.PostgresExecutable;
import ru.yandex.qatools.embed.postgresql.PostgresProcess;
import ru.yandex.qatools.embed.postgresql.PostgresStarter;
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig;

import javax.ws.rs.client.WebTarget;

/**
 * Created by @romansergey on 4/16/16.
 */
public class AppRule implements TestRule {

    private DbResource dbResource = new DbResource();
    private DropwizardAppRule<AppConfiguration> app;

    public DropwizardAppRule<AppConfiguration> getApp() {
        return this.app;
    }

    public WebTarget buildClientTarget() {
        return new JerseyClientBuilder(app.getEnvironment()).build("default")
                .property(ClientProperties.READ_TIMEOUT, 4000)
                .target(String.format("http://localhost:%d", app.getLocalPort()));
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return dbResource.apply(new Statement() {
            @Override
            public void evaluate() throws Throwable {
                AppRule.this.app = new DropwizardAppRule<>(ServerApplication.class, ResourceHelpers.resourceFilePath("test-config.yaml"), new ConfigOverride[] {
                        ConfigOverride.config("database.url", getDbUrl())
                });
                AppRule.this.app.apply(base, description).evaluate();
            }
        }, description);
    }

    public String getDbUrl() {
        return String.format("jdbc:postgresql://%s:%s/%s", dbResource.config().net().host(), dbResource.config().net().port(), dbResource.config().storage().dbName());
    }


}
