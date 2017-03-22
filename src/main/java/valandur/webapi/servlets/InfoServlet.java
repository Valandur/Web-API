package valandur.webapi.servlets;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import valandur.webapi.json.JsonConverter;
import valandur.webapi.misc.Permission;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class InfoServlet extends WebAPIServlet {
    @Override
    @Permission(perm = "info")
    protected void handleGet(ServletData data) {
        data.setStatus(HttpServletResponse.SC_OK);

        Server server = Sponge.getServer();
        Platform platform = Sponge.getPlatform();

        data.addJson("motd", server.getMotd().toPlain());
        data.addJson("players", server.getOnlinePlayers().size());
        data.addJson("maxPlayers", server.getMaxPlayers());
        data.addJson("uptimeTicks", server.getRunningTimeTicks());
        data.addJson("hasWhitelist", server.hasWhitelist());
        data.addJson("minecraftVersion", platform.getMinecraftVersion().getName());

        data.addJson("game", containerToJson(platform.getContainer(Platform.Component.GAME)));
        data.addJson("api", containerToJson(platform.getContainer(Platform.Component.API)));
        data.addJson("implementation", containerToJson(platform.getContainer(Platform.Component.IMPLEMENTATION)));
    }

    private ObjectNode containerToJson(PluginContainer container) {
        ObjectNode obj = JsonNodeFactory.instance.objectNode();
        obj.put("id", container.getId());
        obj.put("name", container.getName());
        Optional<String> version = container.getVersion();
        obj.put("version", version.isPresent() ? version.get() : null);
        Optional<String> descr = container.getDescription();
        obj.put("description", descr.isPresent() ? descr.get() : null);
        Optional<String> url = container.getVersion();
        obj.put("url", url.isPresent() ? url.get() : null);
        obj.set("authors", JsonConverter.toJson(container.getAuthors()));
        return obj;
    }
}
