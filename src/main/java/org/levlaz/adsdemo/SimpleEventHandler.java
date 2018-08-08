package org.levlaz.adsdemo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.MessageEvent;

public class SimpleEventHandler implements EventHandler {
    @Override
    public void onOpen() throws Exception {
        System.out.println("Established Connection with the LaunchDarkly Analytics Data Stream");
    }

    @Override
    public void onClosed() throws Exception {
        System.out.println("onClosed");
    }

    @Override
    public void onMessage(String event, MessageEvent messageEvent) throws Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(messageEvent.getData());
        System.out.println(gson.toJson(je));
    }

    @Override
    public void onComment(String comment) throws Exception {
        System.out.println("onComment");
    }

    @Override
    public void onError(Throwable t) {
        System.out.println("onError: " + t);
    }
}