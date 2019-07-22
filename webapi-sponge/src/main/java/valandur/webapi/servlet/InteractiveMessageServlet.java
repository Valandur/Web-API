package valandur.webapi.servlet;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import valandur.webapi.message.InteractiveMessage;
import valandur.webapi.servlet.base.BaseServlet;
import valandur.webapi.servlet.base.ExplicitDetails;
import valandur.webapi.servlet.base.Permission;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Path("interactive-message")
@Api(tags = { "Interactive Message" }, value = "Send (interactive) messages to clients.")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class InteractiveMessageServlet extends BaseServlet {

    @GET
    @ExplicitDetails
    @Permission("list")
    @ApiOperation(
            value = "List messages",
            notes = "Get a list of all the messages that were sent through the message " +
                    "endpoint since the server started.")
    public List<InteractiveMessage> listMessages() {
        return messageService.getMessages();
    }

    @GET
    @Path("/{uuid}")
    @Permission("one")
    @ApiOperation(
            value = "Get a message",
            notes = "Get detailed information about a message.")
    public InteractiveMessage getMessage(
            @PathParam("uuid") @ApiParam("The uuid of the sent message") UUID uuid)
            throws NotFoundException {
        Optional<InteractiveMessage> optMsg = messageService.getMessage(uuid);
        if (!optMsg.isPresent()) {
            throw new NotFoundException("Message with uuid " + uuid + " not found");
        }

        return optMsg.get();
    }

    @POST
    @Permission("create")
    @ApiOperation(
            value = "Send a message",
            response = InteractiveMessage.class,
            notes = "Send an interactive message to a player. Make sure to have an event hook for " +
                    "\"custom_message\" to receive the response.")
    public Response sendMessage(InteractiveMessage msg)
            throws BadRequestException, URISyntaxException {

        if (msg == null) {
            throw new BadRequestException("Request body is required");
        }

        if (msg.getTarget() == null && (msg.getTargets() == null || msg.getTargets().size() == 0)) {
            throw new BadRequestException("You need to specify either a single target or a list of targets");
        }

        messageService.sendMessage(msg);
        return Response.created(new URI(null, null, msg.getLink(), null)).entity(msg).build();
    }
}
