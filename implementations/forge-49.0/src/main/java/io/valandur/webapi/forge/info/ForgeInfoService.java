package io.valandur.webapi.forge.info;

import io.valandur.webapi.forge.ForgeWebAPI;
import io.valandur.webapi.info.InfoService;
import io.valandur.webapi.info.ServerInfo;

public class ForgeInfoService extends InfoService<ForgeWebAPI> {
    public ForgeInfoService(ForgeWebAPI webapi) {
        super(webapi);
    }

    @Override
    public ServerInfo getInfo() {
        var server = webapi.getPlugin().getServer();

        var motd = server.getMotd();
        var players = server.getPlayerCount();
        var maxPlayers = server.getMaxPlayers();
        var online = server.isPublished();
        var tps = server.getAverageTickTimeNanos();
        var version = server.getServerVersion();

        return new ServerInfo(
                motd,
                players,
                maxPlayers,
                online,
                webapi.getPlugin().getUptime(),
                tps,
                version,
                "forge",
                "",
                webapi.getVersion()
        );
    }

    @Override
    public void startRecording() {
        // TODO
    }

    @Override
    public void stopRecording() {
        // TODO
    }
}
