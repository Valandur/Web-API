package io.valandur.webapi.web;

import io.valandur.webapi.Config;

public interface WebConfig extends Config {

    String defaultBasePath = "/";

    String getBasePath();

    String defaultHost = "0.0.0.0";

    String getHost();

    int defaultPort = 8080;

    int getPort();

    int defaultMinThreads = 2;

    int getMinThreads();

    int defaultMaxThreads = 16;

    int getMaxThreads();

    int defaultIdleTimeout = 60;

    int getIdleTimeout();
}
