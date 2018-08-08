package org.levlaz.adsdemo;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;

import org.flywaydb.core.Flyway;

import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.Headers;

/**
 * LaunchDarkly ADS Consumer Reference Implementation
 *
 */
public class App 
{
    public static void main( String[] args ) throws InterruptedException
    {
        Dotenv dotenv = Dotenv.configure().load();
        Flyway flyway = new Flyway();
        flyway.setDataSource(dotenv.get("FLYWAY_URL"), null, null);

        try {
            flyway.migrate();
        } catch (Exception e) {
            System.out.println("Unable to run Migration");
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
