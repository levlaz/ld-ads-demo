package org.levlaz.adsdemo;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleEventHandler implements EventHandler {

    private static final Logger logger =
        LoggerFactory.getLogger(App.class.getName());
    
    @Override
    public void onOpen() throws Exception {
        logger.info("Established Connection with the LaunchDarkly Analytics Data Stream");
    }

    @Override
    public void onClosed() throws Exception {
        logger.info("onClosed");
    }

    @Override
    public void onMessage(String event, MessageEvent messageEvent) throws Exception {
        System.out.println(messageEvent.getData());
    }

    @Override
    public void onComment(String comment) throws Exception {
        logger.info("onComment");
    }

    @Override
    public void onError(Throwable e) {
        logger.error(e.getMessage(), e);
    }
}