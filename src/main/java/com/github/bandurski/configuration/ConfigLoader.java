package com.github.bandurski.configuration;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ConfigLoader {

    private static final Config config = ConfigFactory.load();

    public static String getApiUrl() {
        return config.getString("api.url");
    }

    public static String getApiToken() {
        return config.getString("api.token");
    }

    public static String getModelName() {
        return config.getString("model.name");
    }

}

