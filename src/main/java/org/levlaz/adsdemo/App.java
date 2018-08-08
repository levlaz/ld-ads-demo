package org.levlaz.adsdemo;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.Headers;

/**
 * LaunchDarkly ADS Consumer Reference Implementation
 *
 */
public class App 
{
    private static final Logger logger =
        LoggerFactory.getLogger(App.class.getName());

    public static void main( String[] args ) throws InterruptedException
    {
        Dotenv dotenv = Dotenv.configure()
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
        
        EventHandler eventHandler = new SimpleEventHandler();
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
        }
    }
}
