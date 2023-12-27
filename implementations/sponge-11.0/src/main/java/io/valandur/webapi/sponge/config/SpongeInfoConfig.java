package io.valandur.webapi.sponge.config;

import io.leangen.geantyref.TypeToken;
import io.valandur.webapi.config.InfoConfig;
import io.valandur.webapi.sponge.SpongeWebAPIPlugin;

public class SpongeInfoConfig extends SpongeConfig implements InfoConfig {

  public SpongeInfoConfig(SpongeWebAPIPlugin plugin) {
    super(plugin, "info.conf");
  }

  @Override
  public int getStatsIntervalSeconds() {
    return get("statsIntervalSeconds", TypeToken.get(Integer.class), defaultStatsIntervalSeconds);
  }

  @Override
  public int getMaxStatsEntries() {
    return get("maxStatsEntries", TypeToken.get(Integer.class), defaultMaxStatsEntries);
  }
}
