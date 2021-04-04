package io.valandur.webapi.server;

import io.valandur.webapi.SpigotWebAPI;
import io.valandur.webapi.info.ServerInfo;

public class SpigotServerService extends ServerService<SpigotWebAPI> {

    public SpigotServerService(SpigotWebAPI webapi) {
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
                server.getVersion()
        );
    }
}
