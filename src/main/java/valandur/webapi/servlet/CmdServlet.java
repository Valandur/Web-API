package valandur.webapi.servlet;

import io.swagger.annotations.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandMapping;
import valandur.webapi.WebAPI;
import valandur.webapi.cache.command.CachedCommand;
import valandur.webapi.command.CommandSource;
import valandur.webapi.security.SecurityContext;
import valandur.webapi.servlet.base.BaseServlet;
import valandur.webapi.servlet.base.ExplicitDetails;
import valandur.webapi.servlet.base.Permission;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
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
    public Collection<CachedCommand> listCommands() {
        return cacheService.getCommands();
    }

    @GET
    @Path("/{cmd}")
    @Permission("one")
    @ApiOperation(
            value = "Get a command",
            notes = "Get detailed information about a command.")
    public CachedCommand getCommand(
            @PathParam("cmd") @ApiParam("The id of the command") String cmdName)
            throws NotFoundException {

        Optional<CachedCommand> cmd = cacheService.getCommand(cmdName);
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
                    "Pass an array of commands to execute them in succession, you can also just pass a list with " +
                    "only one command if that's all you want to execute.\n\n" +
                    "Returns a list with each response corresponding to a command.")
    public List<ExecuteCommandResponse> runCommands(
            List<ExecuteCommandRequest> reqs,
            @Context HttpServletRequest request) {

        if (reqs == null) {
            throw new BadRequestException("Request body is required");
        }

        SecurityContext context = (SecurityContext)request.getAttribute("security");

        List<ExecuteCommandResponse> res = new ArrayList<>();
        for (ExecuteCommandRequest req : reqs) {
            res.add(runCommand(context, req));
        }

        return res;
    }

    private ExecuteCommandResponse runCommand(SecurityContext context, ExecuteCommandRequest req) {
        String cmd = req.getCommand().split(" ")[0];

        Optional<? extends CommandMapping> map = Sponge.getCommandManager().get(cmd);
        if (!map.isPresent()) {
            return new ExecuteCommandResponse(req.command, "Unknown command: " + cmd);
        }

        String name = map.get().getPrimaryAlias();
        if (!context.hasPerms(name)) {
            return new ExecuteCommandResponse(req.getCommand(), "You are not allowed to execute '" + cmd + "'");
        }

        final CommandSource src = new CommandSource(req.getName(), req.getWaitLines(), req.isHiddenInConsole());

        try {
            WebAPI.runOnMain(() -> WebAPI.executeCommand(req.getCommand(), src));

            if (req.getWaitLines() > 0 || req.getWaitTime() > 0) {
                synchronized (src) {
                    src.wait(req.getWaitTime() > 0 ? req.getWaitTime() : CMD_WAIT_TIME);
                }
            }

            return new ExecuteCommandResponse(req.command, src.getLines());
        } catch (InterruptedException e) {
            e.printStackTrace();
            if (WebAPI.reportErrors()) WebAPI.sentryCapture(e);
            return new ExecuteCommandResponse(req.command, e.getMessage());
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

    @ApiModel("ExecuteCommandResponse")
    public static class ExecuteCommandResponse {

        private boolean ok;
        @ApiModelProperty(value = "True if this command executed successfully, false otherwise", required = true)
        public boolean isOk() {
            return ok;
        }

        private String cmd;
        @ApiModelProperty(value = "The command that was executed", required = true)
        public String getCmd() {
            return cmd;
        }

        private String error;
        @ApiModelProperty(value = "Any potential error that occured during execution")
        public String getError() {
            return error;
        }

        private List<String> response;
        @ApiModelProperty(value = "The response chat lines that were sent when executing the command")
        public List<String> getResponse() {
            return response;
        }


        public ExecuteCommandResponse(String cmd, List<String> response) {
            this.cmd = cmd;
            this.ok = true;
            this.response = response;
        }
        public ExecuteCommandResponse(String cmd, String error) {
            this.cmd = cmd;
            this.ok = false;
            this.error = error;
        }
    }
}
