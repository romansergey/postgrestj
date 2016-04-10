package db.structure;

import org.junit.rules.ExternalResource;
import ru.yandex.qatools.embed.postgresql.PostgresExecutable;
import ru.yandex.qatools.embed.postgresql.PostgresProcess;
import ru.yandex.qatools.embed.postgresql.PostgresStarter;
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig;

/**
 * Created by @romansergey on 4/10/16.
 */
public class DbResource extends ExternalResource {

    private PostgresConfig config;
    private PostgresProcess process;

    @Override
    protected void before() throws Throwable {
        PostgresStarter<PostgresExecutable, PostgresProcess> runtime = PostgresStarter.getDefaultInstance();
        this.config = PostgresConfig.defaultWithDbName("test", "test", "test");
        PostgresExecutable exec = runtime.prepare(config);
        this.process = exec.start();
    };

    @Override
    protected void after() {
        this.process.stop();
    };

    public PostgresConfig config() {
        return config;
    }

    public PostgresProcess process() {
        return process;
    }
}
