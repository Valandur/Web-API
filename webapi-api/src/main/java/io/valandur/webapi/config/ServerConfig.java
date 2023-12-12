package io.valandur.webapi.config;

public abstract class ServerConfig implements Config {

  protected String defaultBasePath = "/";
  public abstract String getBasePath();

  protected String defaultHost = "0.0.0.0";
  public abstract String getHost();

  protected int defaultPort = 8080;
  public abstract int getPort();

  protected int defaultMinThreads = 1;
  public abstract int getMinThreads();

  protected int defaultMaxThreads = 4;
  public abstract int getMaxThreads();

  protected int defaultIdleTimeout = 60;
  public abstract int getIdleTimeout();
}
