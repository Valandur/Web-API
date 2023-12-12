package io.valandur.webapi.sponge.server;

import io.valandur.webapi.info.ServerInfo;
import io.valandur.webapi.server.ServerService;
import io.valandur.webapi.sponge.SpongeWebAPI;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.spongepowered.api.Sponge;

public class SpongeServerService extends ServerService<SpongeWebAPI> {

  public SpongeServerService(SpongeWebAPI webapi) {
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
        Sponge.platform().minecraftVersion().name()
    );
  }
}
