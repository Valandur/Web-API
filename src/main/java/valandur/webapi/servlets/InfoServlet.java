package valandur.webapi.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import valandur.webapi.Permission;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class InfoServlet extends WebAPIServlet {
    @Override
    @Permission(perm = "info")
    protected void handleGet(ServletData data) {
        data.setStatus(HttpServletResponse.SC_OK);

        Server server = Sponge.getServer();
        Platform platform = Sponge.getPlatform();

        JsonObject json = data.getJson();
        json.addProperty("motd", server.getMotd().toPlain());
        json.addProperty("players", server.getOnlinePlayers().size());
        json.addProperty("maxPlayers", server.getMaxPlayers());
        json.addProperty("uptimeTicks", server.getRunningTimeTicks());
        json.addProperty("hasWhitelist", server.hasWhitelist());
        json.addProperty("minecraftVersion", platform.getMinecraftVersion().getName());

        json.add("game", containerToJson(platform.getContainer(Platform.Component.GAME)));
        json.add("api", containerToJson(platform.getContainer(Platform.Component.API)));
        json.add("implementation", containerToJson(platform.getContainer(Platform.Component.IMPLEMENTATION)));
    }

    private JsonObject containerToJson(PluginContainer container) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", container.getId());
        obj.addProperty("name", container.getName());
        Optional<String> version = container.getVersion();
        obj.addProperty("version", version.isPresent() ? version.get() : null);
        Optional<String> descr = container.getDescription();
        obj.addProperty("description", descr.isPresent() ? descr.get() : null);
        Optional<String> url = container.getVersion();
        obj.addProperty("url", url.isPresent() ? url.get() : null);
        JsonArray arr = new JsonArray();
        for (String author : container.getAuthors()) {
            arr.add(new JsonPrimitive(author));
        }
        obj.add("authors", arr);
        return obj;
    }
}
