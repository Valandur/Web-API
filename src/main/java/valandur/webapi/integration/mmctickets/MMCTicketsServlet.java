package valandur.webapi.integration.mmctickets;

import net.moddedminecraft.mmctickets.Main;
import net.moddedminecraft.mmctickets.data.TicketData;
import org.eclipse.jetty.http.HttpMethod;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import valandur.webapi.WebAPI;
import valandur.webapi.api.annotation.WebAPIEndpoint;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.servlet.WebAPIBaseServlet;
import valandur.webapi.json.JsonService;
import valandur.webapi.servlet.base.ServletData;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@WebAPIServlet(basePath = "mmctickets")
public class MMCTicketsServlet extends WebAPIBaseServlet {

    public static void onRegister() {
        JsonService json = WebAPI.getJsonService();
        json.registerSerializer(CachedTicket.class, CachedTicketSerializer.class);
    }


    private Main getMMCTicketsPlugin(ServletData data) {
        Optional<PluginContainer> optContainer = Sponge.getPluginManager().getPlugin("mmctickets");
        if (!optContainer.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "MMCTickets plugin not found");
            return null;
        }

        Optional<?> optPlugin = optContainer.get().getInstance();
        if (!optPlugin.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "MMCTickets plugin instance not found");
            return null;
        }

        return (Main)optPlugin.get();
    }

    @WebAPIEndpoint(method = HttpMethod.GET, path = "/ticket", perm = "list")
    public void getTickets(ServletData data) {
        Main plugin = getMMCTicketsPlugin(data);
        if (plugin == null) return;

        Optional<List<CachedTicket>> optTickets = WebAPI.runOnMain(() -> {
            List<CachedTicket> tickets = new ArrayList<>();
            for (TicketData ticket : plugin.getTickets()) {
                tickets.add(new CachedTicket(ticket));
            }
            return tickets;
        });

        data.addJson("ok", optTickets.isPresent(), false);
        data.addJson("tickets", optTickets.orElse(null), data.getQueryParam("details").isPresent());
    }

    @WebAPIEndpoint(method = HttpMethod.GET, path = "/ticket/:id", perm = "one")
    public void getTicket(ServletData data, Integer id) {
        Main plugin = getMMCTicketsPlugin(data);
        if (plugin == null) return;

        Optional<CachedTicket> optTicket = WebAPI.runOnMain(() -> {
            TicketData ticketData = plugin.getTicket(id);
            if (ticketData == null) {
                data.sendError(HttpServletResponse.SC_NOT_FOUND, "Ticket not found");
                return null;
            }

            return new CachedTicket(ticketData);
        });

        data.addJson("ok", optTicket.isPresent(), false);
        data.addJson("ticket", optTicket.orElse(null), true);
    }

    @WebAPIEndpoint(method = HttpMethod.PUT, path = "/ticket/:id", perm = "change")
    public void changeTicket(ServletData data, Integer id) {
        Main plugin = getMMCTicketsPlugin(data);
        if (plugin == null) return;

        Optional<ChangeTicketRequest> optReq = data.getRequestBody(ChangeTicketRequest.class);
        if (!optReq.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ticket data: " + data.getLastParseError().getMessage());
            return;
        }

        ChangeTicketRequest req = optReq.get();

        Optional<CachedTicket> optTicket = WebAPI.runOnMain(() -> {
            TicketData ticketData = plugin.getTicket(id);
            if (ticketData == null) {
                data.sendError(HttpServletResponse.SC_NOT_FOUND, "Ticket not found");
                return null;
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

            return new CachedTicket(ticketData);
        });

        data.addJson("ok", optTicket.isPresent(), false);
        data.addJson("ticket", optTicket.orElse(null), true);
    }
}
