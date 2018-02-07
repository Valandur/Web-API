package valandur.webapi.integration.mmctickets;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.moddedminecraft.mmctickets.Main;
import net.moddedminecraft.mmctickets.data.TicketData;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import valandur.webapi.api.WebAPIAPI;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Permission;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Path("mmc-tickets")
@Api(tags = { "MMC Tickets" }, value = "View, assign and reply-to tickets on the server.")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class MMCTicketsServlet extends BaseServlet {

    public static void onRegister() {
        WebAPIAPI.getJsonService().ifPresent(srv -> {
            srv.registerCache(TicketData.class, CachedTicketData.class);
        });
    }


    private Main getMMCTicketsPlugin() {
        Optional<PluginContainer> optContainer = Sponge.getPluginManager().getPlugin("mmctickets");
        if (!optContainer.isPresent()) {
            throw new InternalServerErrorException("MMCTickets plugin not found");
        }

        Optional<?> optPlugin = optContainer.get().getInstance();
        if (!optPlugin.isPresent()) {
            throw new InternalServerErrorException("MMCTickets plugin instance not found");
        }

        return (Main)optPlugin.get();
    }

    @GET
    @Path("/ticket")
    @Permission({ "ticket", "list" })
    @ApiOperation(value = "List tickets", notes = "Get a list of all the tickets on the server.")
    public Collection<CachedTicketData> getTickets() {
        Main plugin = getMMCTicketsPlugin();

        return WebAPIAPI.runOnMain(() -> {
            List<CachedTicketData> tickets = new ArrayList<>();
            for (TicketData ticket : plugin.getTickets()) {
                tickets.add(new CachedTicketData(ticket));
            }
            return tickets;
        });
    }

    @GET
    @Path("/ticket/{id}")
    @Permission({ "ticket", "one" })
    @ApiOperation(value = "Get a ticket", notes = "Get detailed information about a ticket.")
    public CachedTicketData getTicket(@PathParam("id") Integer id)
            throws NotFoundException {
        return WebAPIAPI.runOnMain(() -> {
            Main plugin = getMMCTicketsPlugin();
            TicketData ticketData = plugin.getTicket(id);
            if (ticketData == null) {
                throw new NotFoundException("Ticket with id " + id + " not found");
            }

            return new CachedTicketData(ticketData);
        });
    }

    @PUT
    @Path("/ticket/{id}")
    @Permission({ "ticket", "modify" })
    @ApiOperation(value = "Modify a ticket", notes = "Modify the properties of an existing ticket.")
    public CachedTicketData modifyTicket(@PathParam("id") Integer id, CachedTicketData req)
            throws NotFoundException {

        if (req == null) {
            throw new BadRequestException("Request body is required");
        }

        return WebAPIAPI.runOnMain(() -> {
            Main plugin = getMMCTicketsPlugin();
            TicketData ticketData = plugin.getTicket(id);
            if (ticketData == null) {
                throw new NotFoundException("Ticket with id " + id + " not found");
            }

            if (req.getComment() != null) {
                ticketData.setComment(req.getComment());
            }
            if (req.getNotified() != null) {
                ticketData.setNotified(req.getNotified());
            }
            if (req.getStatus() != null) {
                ticketData.setStatus(req.getStatus());
            }

            return new CachedTicketData(ticketData);
        });
    }
}
