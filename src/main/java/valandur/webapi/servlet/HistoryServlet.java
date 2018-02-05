package valandur.webapi.servlet;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import valandur.webapi.api.cache.chat.ICachedChatMessage;
import valandur.webapi.api.cache.command.ICachedCommandCall;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Permission;

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
    @Path("/cmd")
    @Permission("cmd")
    @ApiOperation(value = "Get command history", notes = "View a history of the server commands.")
    public List<ICachedCommandCall> getCommands() {
        return cacheService.getCommandCalls();
    }

    @GET
    @Path("/chat")
    @Permission("chat")
    @ApiOperation(value = "Get chat history", notes = "View a history of the server chat.")
    public List<ICachedChatMessage> getChat() {
        return cacheService.getChatMessages();
    }
}
