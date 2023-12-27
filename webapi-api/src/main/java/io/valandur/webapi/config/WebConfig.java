package io.valandur.webapi.config;

public interface WebConfig extends Config {

  String defaultBasePath = "/";

  String getBasePath();

  String defaultHost = "0.0.0.0";

  String getHost();

  int defaultPort = 8080;

  int getPort();

  int defaultMinThreads = 1;

  int getMinThreads();

  int defaultMaxThreads = 4;

  int getMaxThreads();

  int defaultIdleTimeout = 60;

  int getIdleTimeout();
}
