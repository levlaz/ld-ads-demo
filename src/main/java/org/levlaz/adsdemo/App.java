package org.levlaz.adsdemo;

import java.net.URI;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;

import org.flywaydb.core.Flyway;
import org.levlaz.adsdemo.connectors.Console;
import org.levlaz.adsdemo.connectors.Postgres;
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

    public void run() {
        EventHandler eventHandler = null;
        Dotenv dotenv = Dotenv.configure()
            .directory("./")
            .ignoreIfMissing()
            .load();

        if ((dotenv.get("FLYWAY_URL") == null ) || (dotenv.get("FLYWAY_URL").length() == 0))  {
            logger.warn("FLYWAY_URL not set, not running migrations");
        } else {
            Flyway flyway = new Flyway();
            flyway.setDataSource(dotenv.get("FLYWAY_URL"), null, null);

            try {
                flyway.migrate();
            } catch (Exception e) {
                logger.error("Unable to run Migration: " + e);
            }
        }

        switch (connector) {
            case "console":
                logger.info("Starting ADS consumer with console connector.");
                eventHandler = new Console();
                break;
            case "postgres":
                logger.info("Starting ADS consumer with postgres connector.");
                try {
                    eventHandler = new Postgres(dotenv.get("FLYWAY_URL"));
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

        try (EventSource eventSource = builder.build()) {
            eventSource.setReconnectionTimeMs(3000);
            eventSource.start();

            TimeUnit.MINUTES.sleep(10);
        } catch (Exception e) {
            logger.error("Error: " + e);
        }
    }

    public static void main( String[] args )
    {
        CommandLine.run(new App(), System.out, args);
    }
}
