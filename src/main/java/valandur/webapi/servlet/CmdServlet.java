package valandur.webapi.servlet;

import io.swagger.annotations.*;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.command.ICachedCommand;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.ExplicitDetails;
import valandur.webapi.api.servlet.Permission;
import valandur.webapi.command.CommandSource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Path("cmd")
@Api(tags = { "Command" }, value = "List all commands on the server and execute them.")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class CmdServlet extends BaseServlet {

    public static int CMD_WAIT_TIME = 1000;

    @GET
    @ExplicitDetails
    @Permission("list")
    @ApiOperation(
            value = "List commands",
            notes = "Gets a list of all the commands available on the server.")
    public Collection<ICachedCommand> listCommands() {
        return cacheService.getCommands();
    }

    @GET
    @Path("/{cmd}")
    @Permission("one")
    @ApiOperation(
            value = "Get a command",
            notes = "Get detailed information about a command.")
    public ICachedCommand getCommand(
            @PathParam("cmd") @ApiParam("The id of the command") String cmdName)
            throws NotFoundException {
        Optional<ICachedCommand> cmd = cacheService.getCommand(cmdName);
        if (!cmd.isPresent()) {
            throw new NotFoundException("The command '" + cmdName + "' could not be found");
        }

        return cmd.get();
    }

    @POST
    @Permission("run")
    @Permission(value = { "run", "[command]" }, autoCheck = false)
    @ApiOperation(
            value = "Execute a command",
            notes = "Execute a command on the server. (Almost the same as running it from the console).  \n" +
                    "Pass a list of commands to execute them in succession, if only passing one command " +
                    "the array is not required.")
    public void runCommands(ExecuteCommandRequest req) {

        if (req == null) {
            throw new BadRequestException("Request body is required");
        }

        // TODO: Implement executing commands
        // TODO: Check specific command permissions
        /*final JsonNode reqJson = data.getRequestBody();

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
        data.addData("results", res, true);*/
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


    @ApiModel("ExecuteCommandRequest")
    public static class ExecuteCommandRequest {

        private String command;
        @ApiModelProperty(value = "The command to execute", required = true)
        public String getCommand() {
            return command;
        }

        private String name;
        @ApiModelProperty("The name of the source that executes the command")
        public String getName() {
            return name != null ? name : "Web-API";
        }

        private Integer waitTime;
        @ApiModelProperty("The amount of time to wait for a response")
        public Integer getWaitTime() {
            return waitTime != null ? waitTime : CmdServlet.CMD_WAIT_TIME;
        }

        private Integer waitLines;
        @ApiModelProperty("The amount of text lines to wait for in the response")
        public Integer getWaitLines() {
            return waitLines != null ? waitLines : 0;
        }

        private Boolean hideInConsole;
        @ApiModelProperty("True to hide the execution of the command in the console, false otherwise")
        public Boolean isHiddenInConsole() {
            return hideInConsole != null ? hideInConsole : false;
        }
    }
}
