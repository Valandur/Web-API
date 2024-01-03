package io.valandur.webapi.forge.web;

import io.valandur.webapi.forge.config.ForgeConfig;
import io.valandur.webapi.web.WebConfig;

public class ForgeWebConfig extends ForgeConfig implements WebConfig {

    public ForgeWebConfig() {
        super("web.toml");

        build();
    }

    @Override
    public String getBasePath() {
        return defaultBasePath;
    }

    @Override
    public String getHost() {
        return defaultHost;
    }

    @Override
    public int getPort() {
        return defaultPort;
    }

    @Override
    public int getMinThreads() {
        return defaultMinThreads;
    }

    @Override
    public int getMaxThreads() {
        return defaultMaxThreads;
    }

    @Override
    public int getIdleTimeout() {
        return defaultIdleTimeout;
    }
}
