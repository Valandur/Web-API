package valandur.webapi.servlets;

import com.google.gson.JsonObject;
import valandur.webapi.Permission;
import valandur.webapi.WebAPI;
import valandur.webapi.cache.CachedCommand;
import valandur.webapi.cache.DataCache;
import valandur.webapi.json.JsonConverter;
import valandur.webapi.misc.WebAPICommandSource;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.concurrent.*;

public class CmdServlet extends WebAPIServlet {
    @Override
    @Permission(perm = "cmd")
    protected void handleGet(ServletData data) {
        String[] paths = data.getPathParts();

        if (paths.length == 0 || paths[0].isEmpty()) {
            data.setStatus(HttpServletResponse.SC_OK);
            data.addJson("commands", JsonConverter.toJson(DataCache.getCommands()));
            return;
        }

        String pName = paths[0];
        Optional<CachedCommand> cmd = DataCache.getCommand(pName);
        if (!cmd.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        data.setStatus(HttpServletResponse.SC_OK);
        data.addJson("command", JsonConverter.toJson(cmd.get(), true));
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

            data.addJson("response", JsonConverter.toJson(src.getLines(), true));

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
