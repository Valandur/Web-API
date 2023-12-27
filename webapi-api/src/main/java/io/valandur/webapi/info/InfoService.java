package io.valandur.webapi.info;

import com.sun.management.OperatingSystemMXBean;
import io.valandur.webapi.Service;
import io.valandur.webapi.WebAPI;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;

public abstract class InfoService<T extends WebAPI<?>> extends Service<T> {

  protected OperatingSystemMXBean systemMXBean;
  protected int statsIntervalSeconds;
  protected int maxStatsEntries;

  protected Queue<ServerStats> stats = new ConcurrentLinkedQueue<>();
  public List<ServerStats> getStats() {
    return new ArrayList<>(stats);
  }

  public InfoService(T webapi) {
    super(webapi);

    var config = webapi.getInfoConfig();
    try {
      config.load();
    } catch (Exception e) {
      webapi.getLogger().error("Could not load config: " + e.getMessage());
    }

    this.statsIntervalSeconds = config.getStatsIntervalSeconds();
    this.maxStatsEntries = config.getMaxStatsEntries();

    try {
      config.save();
    } catch (Exception e) {
      webapi.getLogger().error("Could not save config: " + e.getMessage());
    }

    systemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
  }

  public abstract ServerInfo getInfo();

  public abstract void startRecording();

  protected void recordStats() {
    long total = 0;
    long free = 0;
    File[] roots = File.listRoots();
    for (File root : roots) {
      total += root.getTotalSpace();
      free += root.getFreeSpace();
    }

    var cpu = systemMXBean.getCpuLoad();

    long maxMem = Runtime.getRuntime().maxMemory();
    long usedMem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());

    try {
      var info = webapi.runOnMain(this::getInfo);

      var newStats = new ServerStats(
          Instant.now(),
          info.playerCount(),
          info.tps(),
          cpu,
          usedMem / (double)maxMem,
          (total - free) / (double)total
      );
      stats.add(newStats);

      while (stats.size() > this.maxStatsEntries) {
        stats.poll();
      }
    } catch (Exception e) {
      webapi.getLogger().warn("Could not get server info: " + e);
    }
  }
}
