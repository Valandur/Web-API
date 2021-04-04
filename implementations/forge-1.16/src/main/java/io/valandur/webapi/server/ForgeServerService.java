package io.valandur.webapi.server;

import io.valandur.webapi.ForgeWebAPI;
import io.valandur.webapi.info.ServerInfo;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class ForgeServerService extends ServerService<ForgeWebAPI> {

    public ForgeServerService(ForgeWebAPI webapi) {
        super(webapi);
    }

    @Override
    public ServerInfo getInfo() {
        var server = ServerLifecycleHooks.getCurrentServer();

        return new ServerInfo(
                server.getMOTD(),
                server.getPlayerList().getCurrentPlayerCount(),
                server.getMaxPlayers(),
                server.isServerInOnlineMode(),
                webapi.getPlugin().getUptime(),
                server.getMinecraftVersion()
        );
    }
}
