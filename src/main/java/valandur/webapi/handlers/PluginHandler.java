package valandur.webapi.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Optional;

public class PluginHandler extends AbstractHandler {
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        JsonObject json = new JsonObject();
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        PluginManager pm = Sponge.getGame().getPluginManager();
        String[] paths = target.substring(1).split("/");
        String pName = paths[0];

        if (pName.isEmpty()) {
            JsonArray arr = new JsonArray();
            Collection<PluginContainer> plugins = pm.getPlugins();
            for (PluginContainer pc : plugins) {
                arr.add(new JsonPrimitive(pc.getId()));
            }
            json.add("plugins", arr);
        } else {
            Optional<PluginContainer> res = pm.getPlugin(pName);
            if (res.isPresent()) {
                PluginContainer plugin = res.get();
                json.addProperty("name", plugin.getName());
                json.addProperty("id", plugin.getId());
                Optional<String> descr = plugin.getDescription();
                json.addProperty("description", descr.isPresent() ? descr.get() : null);
                Optional<String> version = plugin.getVersion();
                json.addProperty("version", version.isPresent() ? version.get() : null);
                Optional<String> url = plugin.getUrl();
                json.addProperty("url", url.isPresent() ? url.get() : null);
                JsonArray arr = new JsonArray();
                for (String author : plugin.getAuthors()) {
                    arr.add(new JsonPrimitive(author));
                }
                json.add("authors", arr);
            } else {
                json.addProperty("error", "Plugin with name " + pName + " not found");
            }
        }

        PrintWriter out = response.getWriter();
        out.print(json);
        baseRequest.setHandled(true);
    }
}
