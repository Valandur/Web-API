package valandur.webapi.servlet;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import valandur.webapi.cache.command.CachedCommandCall;
import valandur.webapi.cache.message.CachedMessage;
import valandur.webapi.servlet.base.BaseServlet;
import valandur.webapi.servlet.base.ExplicitDetails;
import valandur.webapi.servlet.base.Permission;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("history")
@Api(tags = { "History" }, value = "Provides access to the command and chat history.")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class HistoryServlet extends BaseServlet {

    @GET
    @ExplicitDetails
    @Path("/cmd")
    @Permission("cmd")
    @ApiOperation(
            value = "Get command history",
            notes = "View a history of the server commands.")
    public List<CachedCommandCall> getCommands() {
        return cacheService.getCommandCalls();
    }

    @GET
    @ExplicitDetails
    @Path("/message")
    @Permission("message")
    @ApiOperation(
            value = "Get message history",
            notes = "View a history of the server messages.")
    public List<CachedMessage> getChat() {
        return cacheService.getMessages();
    }
}
