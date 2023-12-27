package io.valandur.webapi.spigot.config;

import io.valandur.webapi.config.InfoConfig;

public class SpigotInfoConfig implements InfoConfig {

  @Override
  public void save() throws Exception {

  }

  @Override
  public void load() throws Exception {

  }

  @Override
  public int getStatsIntervalSeconds() {
    return defaultStatsIntervalSeconds;
  }

  @Override
  public int getMaxStatsEntries() {
    return defaultMaxStatsEntries;
  }
}
