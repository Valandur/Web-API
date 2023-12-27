package io.valandur.webapi.sponge.info;

import io.valandur.webapi.info.ServerInfo;
import io.valandur.webapi.info.InfoService;
import io.valandur.webapi.sponge.SpongeWebAPI;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Task;

public class SpongeInfoService extends InfoService<SpongeWebAPI> {

  protected ScheduledTask task;

  public SpongeInfoService(SpongeWebAPI webapi) {
    super(webapi);
  }

  @Override
  public ServerInfo getInfo() {
    var server = Sponge.server();

    return new ServerInfo(
        PlainTextComponentSerializer.plainText().serialize(server.motd()),
        server.onlinePlayers().size(),
        server.maxPlayers(),
        server.isOnlineModeEnabled(),
        webapi.getPlugin().getUptime(),
        server.ticksPerSecond(),
        Sponge.platform().minecraftVersion().name(),
        webapi.getVersion()
    );
  }

  @Override
  public void startRecording() {
    var tsk = Task.builder().execute(this::recordStats)
        .delay(statsIntervalSeconds, TimeUnit.SECONDS)
        .interval(statsIntervalSeconds, TimeUnit.SECONDS)
        .plugin(webapi.getPlugin().getContainer())
        .build();
    task = Sponge.asyncScheduler().submit(tsk, "WebAPI - Stats");
  }

}
