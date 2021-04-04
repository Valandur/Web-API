package io.valandur.webapi.server;

import io.valandur.webapi.SpongeWebAPI;
import io.valandur.webapi.info.ServerInfo;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.adventure.SpongeComponents;

public class SpongeServerService extends ServerService<SpongeWebAPI> {

    public SpongeServerService(SpongeWebAPI webapi) {
        super(webapi);
    }

    @Override
    public ServerInfo getInfo() {
        var server = Sponge.server();

        return new ServerInfo(
                SpongeComponents.plainSerializer().serialize(server.motd()),
                server.onlinePlayers().size(),
                server.maxPlayers(),
                server.isOnlineModeEnabled(),
                webapi.getPlugin().getUptime(),
                Sponge.platform().minecraftVersion().name()
        );
    }
}
