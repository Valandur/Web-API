package valandur.webapi.servlet.info;

import com.fasterxml.jackson.databind.JsonNode;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import valandur.webapi.api.annotation.WebAPIRoute;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.servlet.IServlet;
import valandur.webapi.server.ServerProperties;
import valandur.webapi.servlet.ServletData;

import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;

@WebAPIServlet(basePath = "info")
public class InfoServlet implements IServlet {

    @WebAPIRoute(method = "GET", path = "/", perm = "get")
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

    @WebAPIRoute(method = "GET", path = "/properties", perm = "properties")
    public void getProperties(ServletData data) {
        data.addJson("ok", true, false);
        data.addJson("properties", ServerProperties.getProperties(), true);
    }

    @WebAPIRoute(method = "POST", path = "/properties", perm = "properties")
    public void setProperties(ServletData data) {
        JsonNode body = data.getRequestBody();
        JsonNode props = body.get("properties");

        if (props == null) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid new properties");
            return;
        }

        for (Iterator<String> it = props.fieldNames(); it.hasNext(); ) {
            String key = it.next();
            ServerProperties.setProperty(key, props.get(key).asText());
        }

        data.addJson("ok", true, false);
        data.addJson("properties", ServerProperties.getProperties(), true);
    }
}
