package io.valandur.webapi.fabric.config;

import io.valandur.webapi.config.ServerConfig;

public class FabricServerConfig extends ServerConfig {

  @Override
  public void save() throws Exception {

  }

  @Override
  public void load() throws Exception {

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
