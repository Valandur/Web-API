package valandur.webapi.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.jetty.http.HttpMethod;
import valandur.webapi.WebAPI;
import valandur.webapi.api.servlet.Endpoint;
import valandur.webapi.api.servlet.Servlet;
import valandur.webapi.api.cache.command.ICachedCommand;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.command.CommandSource;
import valandur.webapi.serialize.request.command.ExecuteCommandRequest;
import valandur.webapi.servlet.base.ServletData;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Servlet(basePath = "cmd")
public class CmdServlet extends BaseServlet {

    public static int CMD_WAIT_TIME = 1000;

    @Endpoint(method = HttpMethod.GET, path = "/", perm = "list")
    public void getCommands(ServletData data) {
        data.addData("ok", true, false);
        data.addData("commands", cacheService.getCommands(), data.getQueryParam("details").isPresent());
    }

    @Endpoint(method = HttpMethod.GET, path = "/:cmd", perm = "one")
    public void getCommand(ServletData data, String cmdName) {
        Optional<ICachedCommand> cmd = cacheService.getCommand(cmdName);
        if (!cmd.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "The command '" + cmdName + "' could not be found");
            return;
        }

        data.addData("ok", true, false);
        data.addData("command", cmd.get(), true);
    }

    @Endpoint(method = HttpMethod.POST, path = "/", perm = "run")
    public void runCommands(ServletData data) {
        final JsonNode reqJson = data.getRequestBody();

        if (reqJson == null) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid command data: " + data.getLastParseError().getMessage());
            return;
        }

        if (!reqJson.isArray()) {
            Optional<ExecuteCommandRequest> optReq = data.getRequestBody(ExecuteCommandRequest.class);
            if (!optReq.isPresent()) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid command data: " + data.getLastParseError().getMessage());
                return;
            }

            ExecuteCommandRequest req = optReq.get();

            if (req.getCommand() == null) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST,"Command not specified");
                return;
            }

            String cmd = req.getCommand().split(" ")[0];
            if (!permissionService.permits(data.getPermissions(), new String[]{ cmd })) {
                data.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                return;
            }

            data.addData("ok", true, false);
            data.addData("result", runCommand(req), true);
            return;
        }

        List<Object> res = new ArrayList<>();
        Optional<ExecuteCommandRequest[]> optReqs = data.getRequestBody(ExecuteCommandRequest[].class);
        if (!optReqs.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid command data: " + data.getLastParseError().getMessage());
            return;
        }

        ExecuteCommandRequest[] reqs = optReqs.get();
        for (ExecuteCommandRequest req : reqs) {
            String cmd = req.getCommand().split(" ")[0];
            if (!permissionService.permits(data.getPermissions(), new String[]{ cmd })) {
                res.add(new Exception("Not allowed"));
                continue;
            }

            res.add(runCommand(req));
        }

        data.addData("ok", true, false);
        data.addData("results", res, true);
    }

    private List<String> runCommand(ExecuteCommandRequest req) {
        final CommandSource src = new CommandSource(req.getName(), req.getWaitLines(), req.isHiddenInConsole());

        try {
            WebAPI.runOnMain(() -> WebAPI.executeCommand(req.getCommand(), src));

            if (req.getWaitLines() > 0 || req.getWaitTime() > 0) {
                synchronized (src) {
                    src.wait(req.getWaitTime() > 0 ? req.getWaitTime() : CMD_WAIT_TIME);
                }
            }

            return src.getLines();
        } catch (InterruptedException e) {
            e.printStackTrace();
            if (WebAPI.reportErrors()) WebAPI.sentryCapture(e);
            return null;
        }
    }
}
