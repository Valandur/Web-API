package valandur.webapi.integration.mmctickets;

import net.moddedminecraft.mmctickets.data.TicketData;
import net.moddedminecraft.mmctickets.data.ticketStatus;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.player.ICachedPlayer;
import valandur.webapi.api.cache.world.ICachedWorld;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedLocation;

public class CachedTicket extends CachedObject {

    private int id;
    public int getId() {
        return id;
    }

    private ICachedPlayer sender;
    public ICachedPlayer getSender() {
        return sender;
    }

    private long timestamp;
    public long getTimestamp() {
        return timestamp;
    }

    private String message;
    public String getMessage() {
        return message;
    }

    private String comment;
    public String getComment() {
        return comment;
    }

    private ticketStatus status;
    public ticketStatus getStatus() {
        return status;
    }

    private ICachedPlayer staff;
    public ICachedPlayer getStaff() {
        return staff;
    }

    private int notified;
    public int getNotified() {
        return notified;
    }

    private CachedLocation location;
    public CachedLocation getLocation() {
        return location;
    }


    public CachedTicket(TicketData ticket) {
        super(ticket);

        this.id = ticket.getTicketID();
        this.sender = WebAPI.getCacheService().getPlayer(ticket.getPlayerUUID()).orElse(null);
        this.timestamp = ticket.getTimestamp();
        this.message = ticket.getMessage();
        this.comment = ticket.getComment();
        this.status = ticket.getStatus();
        this.staff = WebAPI.getCacheService().getPlayer(ticket.getPlayerUUID()).orElse(null);
        this.notified = ticket.getNotified();

        ICachedWorld world = WebAPI.getCacheService().getWorld(ticket.getWorld()).orElse(null);
        this.location = new CachedLocation(world, ticket.getX(), ticket.getY(), ticket.getZ());
    }
}
