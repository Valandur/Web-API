package valandur.webapi.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.text.Text;
import valandur.webapi.Permission;
import valandur.webapi.WebAPI;
import valandur.webapi.cache.CachedCommandCall;
import valandur.webapi.cache.DataCache;
import valandur.webapi.misc.JsonConverter;
import valandur.webapi.misc.WebAPICommandSource;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;

public class CmdServlet extends WebAPIServlet {
    @Override
    @Permission(perm = "cmd")
    protected void handleGet(ServletData data) {
        data.setStatus(HttpServletResponse.SC_OK);
        String[] paths = data.getPathParts();

        if (paths.length > 0 && paths[0].equalsIgnoreCase("history")) {
            JsonArray arr = new JsonArray();
            for (CachedCommandCall msg : DataCache.getCommandCalls()) {
                arr.add(JsonConverter.cacheToJson(msg, true));
            }
            data.getJson().add("calls", arr);
        } else {
            WebAPICommandSource cmdSource = new WebAPICommandSource();

            JsonArray arr = new JsonArray();
            Collection<CommandMapping> cmds = Sponge.getCommandManager().getAll().values();
            for (CommandMapping cmd : cmds) {
                JsonObject obj = new JsonObject();
                obj.addProperty("name", cmd.getPrimaryAlias());
                Set<String> aliases = cmd.getAllAliases();
                JsonArray jsonAliases = new JsonArray();
                for (String alias : aliases) {
                    jsonAliases.add(new JsonPrimitive(alias));
                }
                obj.add("aliases", jsonAliases);
                obj.addProperty("usage", cmd.getCallable().getUsage(cmdSource).toPlain());
                Optional<Text> descr = cmd.getCallable().getShortDescription(cmdSource);
                obj.addProperty("description", descr.isPresent() ? descr.get().toPlain() : null);

                arr.add(obj);
            }
            data.getJson().add("commands", arr);
        }
    }

    @Override
    @Permission(perm = "cmd")
    protected void handlePost(ServletData data) {
        data.setStatus(HttpServletResponse.SC_OK);

        final JsonObject reqJson = (JsonObject) data.getAttribute("body");
        final String cmd = reqJson.get("command").getAsString();
        final String name = reqJson.has("name") ? reqJson.get("name").getAsString() : WebAPI.NAME;
        final int waitTime = reqJson.has("waitTime") ? reqJson.get("waitTime").getAsInt() : 0;
        final int waitLines = reqJson.has("waitLines") ? reqJson.get("waitLines").getAsInt() : 0;

        final WebAPICommandSource src = new WebAPICommandSource(name, waitLines);

        try {
            CompletableFuture
                .runAsync(() -> WebAPI.executeCommand(cmd, src), WebAPI.syncExecutor)
                .get();

            if (waitLines > 0 || waitTime > 0) {
                synchronized (src) {
                    src.wait(waitTime > 0 ? waitTime : WebAPI.cmdWaitTime);
                }
            }

            JsonArray arr = new JsonArray();
            for (String line : src.getLines()) {
                arr.add(new JsonPrimitive(line));
            }
            data.getJson().add("response", arr);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
