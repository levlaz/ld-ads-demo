package org.levlaz.adsdemo.utils;

import com.amazonaws.ClientConfiguration;

/**
 * Provides configuration related utilities for Kinesis
 */
public class KinesisConfigurationUtils {

    private static final String APPLICATION_NAME = "ld-ads-demo";
    private static final String VERSION = "1.0.0";
    
    public static ClientConfiguration getClientConfig() {
        final ClientConfiguration config = new ClientConfiguration();
        final StringBuilder userAgent = new StringBuilder(ClientConfiguration.DEFAULT_USER_AGENT);

        userAgent.append(" ");
        userAgent.append(APPLICATION_NAME);
        userAgent.append("/");
        userAgent.append(VERSION);

        config.setUserAgentPrefix(userAgent.toString());
        config.setUserAgentSuffix(null);

        return config;
    }
}