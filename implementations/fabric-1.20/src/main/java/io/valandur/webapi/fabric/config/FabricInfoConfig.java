package io.valandur.webapi.fabric.config;

import io.valandur.webapi.info.InfoConfig;

public class FabricInfoConfig implements InfoConfig {

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
