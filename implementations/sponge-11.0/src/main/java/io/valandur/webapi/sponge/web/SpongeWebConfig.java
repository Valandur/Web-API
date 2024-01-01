package io.valandur.webapi.sponge.web;

import io.leangen.geantyref.TypeToken;
import io.valandur.webapi.sponge.config.SpongeConfig;
import io.valandur.webapi.web.WebConfig;
import io.valandur.webapi.sponge.SpongeWebAPIPlugin;

public class SpongeWebConfig extends SpongeConfig implements WebConfig {

  public SpongeWebConfig(SpongeWebAPIPlugin plugin) {
    super(plugin, "web.conf");
  }

  @Override
  public String getBasePath() {
    return get("path", TypeToken.get(String.class), defaultBasePath);
  }

  @Override
  public String getHost() {
    return get("host", TypeToken.get(String.class), defaultHost);
  }

  @Override
  public int getPort() {
    return get("port", TypeToken.get(Integer.class), defaultPort);
  }

  @Override
  public int getMinThreads() {
    return get("minThreads", TypeToken.get(Integer.class), defaultMinThreads);
  }

  @Override
  public int getMaxThreads() {
    return get("maxThreads", TypeToken.get(Integer.class), defaultMaxThreads);
  }

  @Override
  public int getIdleTimeout() {
    return get("idleTimeout", TypeToken.get(Integer.class), defaultIdleTimeout);
  }
}
