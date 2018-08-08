package org.levlaz;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;

import okhttp3.Headers;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws InterruptedException
    {
        System.out.println( "Hello World!" );
        EventHandler eventHandler = new SimpleEventHandler();
        String url = String.format("https://firehose.launchdarkly.com");
        Headers headers = new Headers.Builder()
            .add("Authorization", System.getenv("LD_SDK_KEY"))
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
