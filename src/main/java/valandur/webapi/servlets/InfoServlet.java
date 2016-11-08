package valandur.webapi.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import valandur.webapi.Permission;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

public class InfoServlet extends APIServlet {
    @Override
    @Permission(perm = "info")
    protected void handleGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);

        Server server = Sponge.getServer();
        Platform platform = Sponge.getPlatform();

        JsonObject json = new JsonObject();
        json.addProperty("motd", server.getMotd().toPlain());
        json.addProperty("players", server.getOnlinePlayers().size());
        json.addProperty("maxPlayers", server.getMaxPlayers());
        json.addProperty("uptimeTicks", server.getRunningTimeTicks());
        json.addProperty("hasWhitelist", server.hasWhitelist());
        json.addProperty("minecraftVersion", platform.getMinecraftVersion().getName());

        PluginContainer api = platform.getApi();
        JsonObject obj = new JsonObject();
        obj.addProperty("id", api.getId());
        obj.addProperty("name", api.getName());
        Optional<String> version = api.getVersion();
        obj.addProperty("version", version.isPresent() ? version.get() : null);
        Optional<String> descr = api.getDescription();
        Optional<String> mcVersion = api.getMinecraftVersion();
        obj.addProperty("minecraftVersion", mcVersion.isPresent() ? mcVersion.get() : null);
        obj.addProperty("description", descr.isPresent() ? descr.get() : null);
        Optional<String> url = api.getVersion();
        obj.addProperty("url", url.isPresent() ? url.get() : null);
        JsonArray arr = new JsonArray();
        for (String author : api.getAuthors()) {
            arr.add(new JsonPrimitive(author));
        }
        obj.add("authors", arr);

        json.add("api", obj);

        PrintWriter out = resp.getWriter();
        out.print(json);
    }
}
