package valandur.webapi.servlet.cmd;

import com.fasterxml.jackson.databind.JsonNode;
import valandur.webapi.WebAPI;
import valandur.webapi.api.annotation.WebAPIRoute;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.cache.command.CachedCommand;
import valandur.webapi.api.permission.Permissions;
import valandur.webapi.api.servlet.WebAPIBaseServlet;
import valandur.webapi.command.CommandSource;
import valandur.webapi.servlet.ServletData;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@WebAPIServlet(basePath = "cmd")
public class CmdServlet extends WebAPIBaseServlet {

    public static int CMD_WAIT_TIME = 1000;

    @WebAPIRoute(method = "GET", path = "/", perm = "list")
    public void getCommands(ServletData data) {
        data.addJson("ok", true, false);
        data.addJson("commands", cacheService.getCommands(), data.getQueryParam("details").isPresent());
    }

    @WebAPIRoute(method = "GET", path = "/:cmd", perm = "one")
    public void getCommand(ServletData data, String cmdName) {
        Optional<CachedCommand> cmd = cacheService.getCommand(cmdName);
        if (!cmd.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "The command '" + cmdName + "' could not be found");
            return;
        }

        data.addJson("ok", true, false);
        data.addJson("command", cmd.get(), true);
    }

    @WebAPIRoute(method = "POST", path = "/", perm = "run")
    public void runCommands(ServletData data) {
        final JsonNode reqJson = data.getRequestBody();

        if (!reqJson.isArray()) {
            String cmd = reqJson.get("command").asText().split(" ")[0];
            if (!Permissions.permits(data.getPermissions(), new String[]{ cmd })) {
                data.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                return;
            }

            data.addJson("ok", true, false);
            data.addJson("result", runCommand(reqJson), true);
            return;
        }

        List<Object> res = new ArrayList<>();
        for (JsonNode node : reqJson) {
            String cmd = node.get("command").asText().split(" ")[0];
            if (!Permissions.permits(data.getPermissions(), new String[]{ cmd })) {
                res.add(new Exception("Not allowed"));
                continue;
            }

            res.add(runCommand(node));
        }

        data.addJson("ok", true, false);
        data.addJson("results", res, true);
    }

    private List<String> runCommand(JsonNode node) {
        final String cmd = node.get("command").asText();
        final String name = node.has("name") ? node.get("name").asText() : WebAPI.NAME;
        final int waitTime = node.has("waitTime") ? node.get("waitTime").asInt() : 0;
        final int waitLines = node.has("waitLines") ? node.get("waitLines").asInt() : 0;

        final CommandSource src = new CommandSource(name, waitLines);

        try {
            WebAPI.runOnMain(() -> WebAPI.executeCommand(cmd, src));

            if (waitLines > 0 || waitTime > 0) {
                synchronized (src) {
                    src.wait(waitTime > 0 ? waitTime : CMD_WAIT_TIME);
                }
            }

            return src.getLines();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
