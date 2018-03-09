package valandur.webapi.servlet;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import valandur.webapi.api.cache.command.ICachedCommandCall;
import valandur.webapi.api.cache.message.ICachedMessage;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.ExplicitDetails;
import valandur.webapi.api.servlet.Permission;

import javax.ws.rs.*;
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
    public List<ICachedCommandCall> getCommands() {
        return cacheService.getCommandCalls();
    }

    @GET
    @ExplicitDetails
    @Path("/message")
    @Permission("message")
    @ApiOperation(
            value = "Get message history",
            notes = "View a history of the server messages.")
    public List<ICachedMessage> getChat() {
        return cacheService.getMessages();
    }
}
