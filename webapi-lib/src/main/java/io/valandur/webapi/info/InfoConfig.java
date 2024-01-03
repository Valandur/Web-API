package io.valandur.webapi.info;

import io.valandur.webapi.Config;

public interface InfoConfig extends Config {

  int defaultStatsIntervalSeconds = 5;

  int getStatsIntervalSeconds();

  int defaultMaxStatsEntries = 4320;

  int getMaxStatsEntries();

}
