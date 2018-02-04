package valandur.webapi.servlet;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import valandur.webapi.api.message.IMessage;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.ExplicitDetails;
import valandur.webapi.api.servlet.Permission;
import valandur.webapi.message.Message;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Path("message")
@Api(value = "message", tags = { "Message" })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class MessageServlet extends BaseServlet {

    @GET
    @ExplicitDetails
    @Permission("list")
    @ApiOperation(value = "Get sent messages", notes =
            "Get a list of all the messages that were sent through the message endpoint since the server started.")
    public List<IMessage> getMessages() {
        return messageService.getMessages();
    }

    @GET
    @Path("/{uuid}")
    @Permission("one")
    @ApiOperation(value = "Get a message", notes = "Get detailed information about a message.")
    public IMessage getMessage(
            @PathParam("uuid") @ApiParam("The uuid of the sent message") UUID uuid)
            throws NotFoundException {
        Optional<IMessage> optMsg = messageService.getMessage(uuid);
        if (!optMsg.isPresent()) {
            throw new NotFoundException("Message with uuid " + uuid + " not found");
        }

        return optMsg.get();
    }

    @POST
    @Permission("create")
    @ApiOperation(value = "Send a message", notes = "Send an interactive message to a player. Make sure to " +
            "have an event hook for \"custom_message\" to receive the response.")
    public IMessage sendMessage(Message msg)
            throws BadRequestException, InternalServerErrorException {
        if (msg.getTarget() == null && (msg.getTargets() == null || msg.getTargets().size() == 0)) {
            throw new BadRequestException("You need to specify either a single target or a list of targets");
        }

        messageService.sendMessage(msg);
        return msg;
    }
}
