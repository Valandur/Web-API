package valandur.webapi.servlets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import valandur.webapi.misc.Permission;
import valandur.webapi.WebAPI;
import valandur.webapi.cache.CachedCommand;
import valandur.webapi.cache.DataCache;
import valandur.webapi.json.JsonConverter;
import valandur.webapi.misc.WebAPICommandSource;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
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

        String cName = paths[0];
        Optional<CachedCommand> cmd = DataCache.getCommand(cName);
        if (!cmd.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "The command '" + cName + "' could not be found");
            return;
        }

        data.setStatus(HttpServletResponse.SC_OK);
        data.addJson("command", JsonConverter.toJson(cmd.get(), true));
    }

    @Override
    @Permission(perm = "cmd")
    protected void handlePost(ServletData data) {
        data.setStatus(HttpServletResponse.SC_OK);

        List<String> permissions = (List<String>) data.getAttribute("perms");
        boolean allowAll = permissions.contains("*") || permissions.contains("cmd.*");

        final JsonNode reqJson = (JsonNode) data.getAttribute("body");

        if (!reqJson.isArray()) {
            String cmd = reqJson.get("command").asText().split(" ")[0];
            if (!allowAll && !permissions.contains("cmd." + cmd)) {
                data.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                return;
            }

            data.addJson("result", runCommand(reqJson));
            return;
        }

        ArrayNode arr = JsonNodeFactory.instance.arrayNode();
        for (JsonNode node : reqJson) {
            String cmd = node.get("command").asText().split(" ")[0];
            if (!allowAll && !permissions.contains("cmd." + cmd)) {
                arr.add(JsonConverter.toJson(HttpServletResponse.SC_FORBIDDEN));
                continue;
            }

            JsonNode res = runCommand(node);
            arr.add(res);
        }
        data.addJson("results", arr);
    }

    private JsonNode runCommand(JsonNode node) {
        final String cmd = node.get("command").asText();
        final String name = node.has("name") ? node.get("name").asText() : WebAPI.NAME;
        final int waitTime = node.has("waitTime") ? node.get("waitTime").asInt() : 0;
        final int waitLines = node.has("waitLines") ? node.get("waitLines").asInt() : 0;

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

            return JsonConverter.toJson(src.getLines(), true);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
}
