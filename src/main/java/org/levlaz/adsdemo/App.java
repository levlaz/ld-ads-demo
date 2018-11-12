package org.levlaz.adsdemo;

import java.net.URI;
import java.sql.SQLException;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.levlaz.adsdemo.connectors.Console;
import org.levlaz.adsdemo.connectors.Postgres;
import org.levlaz.adsdemo.connectors.MySQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.Headers;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * LaunchDarkly ADS Consumer Reference Implementation
 *
 */
@Command(name = "ld-ads-demo", mixinStandardHelpOptions = true)
public class App implements Runnable {
    @Option(names = {"-c" , "--connector"}, description = "Connector to Use")
    String connector = "default";

    private static final Logger logger =
        LoggerFactory.getLogger(App.class.getName());

    private void runMigrations(String databaseType, String flywayUrl) {
        FluentConfiguration configuration = new FluentConfiguration();
        configuration.dataSource(flywayUrl, null, null);

        if (databaseType == "mysql") {
            configuration.locations("db/migration/mysql");
        } else if (databaseType == "postgres") {
            configuration.locations("db/migration/postgres");
        } else {
            logger.error("Unkown Database Type");
            System.exit(1);
        }
        
        Flyway flyway = new Flyway(configuration);

        try {
            flyway.migrate();
        } catch (Exception e) {
            logger.error("Unable to run migration: " + e);
        }
    }

    public void run() {
        EventHandler eventHandler = null;
        Dotenv dotenv = Dotenv.configure()
            .directory("./")
            .ignoreIfMissing()
            .load();

        switch (connector) {
            case "console":
                logger.info("Starting ADS consumer with console connector.");
                eventHandler = new Console();
                break;
            case "postgres":
                logger.info("Starting ADS consumer with postgres connector.");
                try {
                    runMigrations("postgres", dotenv.get("FLYWAY_URL"));
                    eventHandler = new Postgres(dotenv.get("FLYWAY_URL"));
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
                break;
            case "mysql":
                logger.info("Starting ADS consumer with MySQL connector.");
                try {
                    runMigrations("mysql", dotenv.get("FLYWAY_URL"));
                    eventHandler = new MySQL(dotenv.get("FLYWAY_URL"));
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
                break;
            case "default":
                logger.info("Starting ADS consumer with default connector.");
                eventHandler = new SimpleEventHandler();
                break;
            default: 
                logger.error("Invalid Connector, falling back to default.");
                break;
        }

        String url = String.format("https://firehose.launchdarkly.com");
        Headers headers = new Headers.Builder()
            .add("Authorization", dotenv.get("LD_SDK_KEY"))
            .add("Accept", "text/event-stream")
            .build();

        EventSource.Builder builder = new EventSource.Builder(eventHandler, URI.create(url))
            .headers(headers);

        try {
            EventSource eventSource = builder.build();
            eventSource.setReconnectionTimeMs(3000);
            eventSource.start();
        } catch (Exception e) {
            logger.error("Error: " + e);
        }
    }

    public static void main( String[] args ) throws InterruptedException
    {
        CommandLine.run(new App(), System.out, args);

        while (true) {
            Thread.sleep(1000);
        }
    }
}
