package org.levlaz.adsdemo.connectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.launchdarkly.eventsource.MessageEvent;

import org.levlaz.adsdemo.SimpleEventHandler;

public class Console extends SimpleEventHandler {

    @Override
    public void onMessage(String event, MessageEvent messageEvent) throws Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(messageEvent.getData());
        System.out.println(gson.toJson(je));
    }
}