package valandur.webapi.integration.mmctickets;

import net.moddedminecraft.mmctickets.Main;
import net.moddedminecraft.mmctickets.data.TicketData;
import org.eclipse.jetty.http.HttpMethod;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import valandur.webapi.api.WebAPIAPI;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Endpoint;
import valandur.webapi.api.servlet.IServletData;
import valandur.webapi.api.servlet.Servlet;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Servlet(basePath = "mmctickets")
public class MMCTicketsServlet extends BaseServlet {

    public static void onRegister() {
        WebAPIAPI.getJsonService().ifPresent(srv -> {
            srv.registerCache(TicketData.class, CachedTicketData.class);
        });
    }


    private Main getMMCTicketsPlugin(IServletData data) {
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

    @Endpoint(method = HttpMethod.GET, path = "/ticket", perm = "list")
    public void getTickets(IServletData data) {
        Main plugin = getMMCTicketsPlugin(data);
        if (plugin == null) return;

        Optional<List<CachedTicketData>> optTickets = WebAPIAPI.runOnMain(() -> {
            List<CachedTicketData> tickets = new ArrayList<>();
            for (TicketData ticket : plugin.getTickets()) {
                tickets.add(new CachedTicketData(ticket));
            }
            return tickets;
        });

        data.addJson("ok", optTickets.isPresent(), false);
        data.addJson("tickets", optTickets.orElse(null), data.getQueryParam("details").isPresent());
    }

    @Endpoint(method = HttpMethod.GET, path = "/ticket/:id", perm = "one")
    public void getTicket(IServletData data, Integer id) {
        Main plugin = getMMCTicketsPlugin(data);
        if (plugin == null) return;

        Optional<CachedTicketData> optTicket = WebAPIAPI.runOnMain(() -> {
            TicketData ticketData = plugin.getTicket(id);
            if (ticketData == null) {
                data.sendError(HttpServletResponse.SC_NOT_FOUND, "Ticket not found");
                return null;
            }

            return new CachedTicketData(ticketData);
        });

        data.addJson("ok", optTicket.isPresent(), false);
        data.addJson("ticket", optTicket.orElse(null), true);
    }

    @Endpoint(method = HttpMethod.PUT, path = "/ticket/:id", perm = "change")
    public void changeTicket(IServletData data, Integer id) {
        Main plugin = getMMCTicketsPlugin(data);
        if (plugin == null) return;

        Optional<ChangeTicketRequest> optReq = data.getRequestBody(ChangeTicketRequest.class);
        if (!optReq.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ticket data: " + data.getLastParseError().getMessage());
            return;
        }

        ChangeTicketRequest req = optReq.get();

        Optional<CachedTicketData> optTicket = WebAPIAPI.runOnMain(() -> {
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

            return new CachedTicketData(ticketData);
        });

        data.addJson("ok", optTicket.isPresent(), false);
        data.addJson("ticket", optTicket.orElse(null), true);
    }
}
