package io.valandur.webapi.fabric.info;

import io.valandur.webapi.fabric.FabricWebAPI;
import io.valandur.webapi.info.ServerInfo;
import io.valandur.webapi.info.InfoService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FabricInfoService extends InfoService<FabricWebAPI> {

  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  public FabricInfoService(FabricWebAPI webapi) {
    super(webapi);
  }

  @Override
  public ServerInfo getInfo() {
    var server = webapi.getPlugin().getServer();

    return new ServerInfo(
        server.getServerMotd(),
        server.getCurrentPlayerCount(),
        server.getMaxPlayerCount(),
        server.isOnlineMode(),
        webapi.getPlugin().getUptime(),
        server.getTickManager().getTickRate(),
        server.getVersion()
    );
  }

  @Override
  public void startRecording() {
    scheduler.scheduleAtFixedRate(this::recordStats, statsIntervalSeconds, statsIntervalSeconds,
        TimeUnit.SECONDS);
  }
}
