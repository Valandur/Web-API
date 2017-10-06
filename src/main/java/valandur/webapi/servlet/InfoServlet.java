package valandur.webapi.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.jetty.http.HttpMethod;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import valandur.webapi.WebAPI;
import valandur.webapi.api.servlet.Endpoint;
import valandur.webapi.api.servlet.Servlet;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.servlet.base.ServletData;

import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Optional;

@Servlet(basePath = "info")
public class InfoServlet extends BaseServlet {

    @Endpoint(method = HttpMethod.GET, path = "/", perm = "get")
    public void getInfo(ServletData data) {
        Optional<Boolean> optRes = WebAPI.runOnMain(() -> {
            Server server = Sponge.getServer();
            Platform platform = Sponge.getPlatform();

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
            return true;
        });

        data.addJson("ok", optRes.orElse(false), false);
    }

    @Endpoint(method = HttpMethod.GET, path = "/properties", perm = "properties")
    public void getProperties(ServletData data) {
        data.addJson("ok", true, false);
        data.addJson("properties", serverService.getProperties(), true);
    }

    @Endpoint(method = HttpMethod.POST, path = "/properties", perm = "properties")
    public void setProperties(ServletData data) {
        JsonNode body = data.getRequestBody();
        JsonNode props = body.get("properties");

        if (props == null) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid new properties");
            return;
        }

        for (Iterator<String> it = props.fieldNames(); it.hasNext(); ) {
            String key = it.next();
            serverService.setProperty(key, props.get(key).asText());
        }

        data.addJson("ok", true, false);
        data.addJson("properties", serverService.getProperties(), true);
    }

    @Endpoint(method = HttpMethod.GET, path="/tps", perm = "tps")
    public void getTps(ServletData data) {
        data.addJson("ok", true, false);
        data.addJson("tps", serverService.getAverageTps(), false);
    }

    @Endpoint(method = HttpMethod.GET, path="/player", perm = "players")
    public void getPlayers(ServletData data) {
        data.addJson("ok", true, false);
        data.addJson("players", serverService.getOnlinePlayers(), false);
    }
}
