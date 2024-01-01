package io.valandur.webapi.spigot.info;

import io.valandur.webapi.info.ServerInfo;
import io.valandur.webapi.info.InfoService;
import io.valandur.webapi.spigot.SpigotWebAPI;
import org.bukkit.scheduler.BukkitTask;

public class SpigotInfoService extends InfoService<SpigotWebAPI> {

    private BukkitTask task;

    public SpigotInfoService(SpigotWebAPI webapi) {
        super(webapi);
    }

    @Override
    public ServerInfo getInfo() {
        var server = webapi.getPlugin().getServer();
        return new ServerInfo(
                server.getMotd(),
                server.getOnlinePlayers().size(),
                server.getMaxPlayers(),
                server.getOnlineMode(),
                webapi.getPlugin().getUptime(),
                0,
                server.getVersion(),
                "spigot",
                server.getBukkitVersion(),
                webapi.getVersion()
        );
    }

    @Override
    public void startRecording() {
        task = webapi.getPlugin().getServer().getScheduler()
                .runTaskTimerAsynchronously(webapi.getPlugin(), this::recordStats,
                        20L * statsIntervalSeconds, 20L * statsIntervalSeconds);
    }

    @Override
    public void stopRecording() {
        task.cancel();
    }
}
