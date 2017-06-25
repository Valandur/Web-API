package valandur.webapi.servlet;

import org.spongepowered.api.Platform;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import valandur.webapi.annotation.WebAPISpec;

public class InfoServlet extends WebAPIServlet {

    @WebAPISpec(method = "GET", path = "/", perm = "info.get")
    public void getInfo(ServletData data) {
        Server server = Sponge.getServer();
        Platform platform = Sponge.getPlatform();

        data.addJson("ok", true, false);

        data.addJson("motd", server.getMotd().toPlain(), false);
        data.addJson("players", server.getOnlinePlayers().size(), false);
        data.addJson("maxPlayers", server.getMaxPlayers(), false);
        data.addJson("address", server.getBoundAddress().map(Object::toString).orElse(null), false);
        data.addJson("onlineMode", server.getOnlineMode(), false);
        data.addJson("resourcePack", server.getDefaultResourcePack().orElse(null), false);
        data.addJson("hasWhitelist", server.hasWhitelist(), false);

        data.addJson("uptimeTicks", server.getRunningTimeTicks(), false);
        data.addJson("tps", server.getTicksPerSecond(), false);
        data.addJson("minecraftVersion", platform.getMinecraftVersion().getName(), false);

        data.addJson("game", platform.getContainer(Platform.Component.GAME), true);
        data.addJson("api", platform.getContainer(Platform.Component.API), true);
        data.addJson("implementation", platform.getContainer(Platform.Component.IMPLEMENTATION), true);
    }
}
